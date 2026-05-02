package dev.thiagooliveira.tablesplit.application.report;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.report.spec.v1.model.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GetReportsOverview {

  private final OrderRepository orderRepository;
  private final FeedbackRepository feedbackRepository;
  private final TableRepository tableRepository;
  private final ItemRepository itemRepository;
  private final PromotionRepository promotionRepository;

  public GetReportsOverview(
      OrderRepository orderRepository,
      FeedbackRepository feedbackRepository,
      TableRepository tableRepository,
      ItemRepository itemRepository,
      PromotionRepository promotionRepository) {
    this.orderRepository = orderRepository;
    this.feedbackRepository = feedbackRepository;
    this.tableRepository = tableRepository;
    this.itemRepository = itemRepository;
    this.promotionRepository = promotionRepository;
  }

  public ReportsOverviewResponse execute(UUID restaurantId, int days) {
    ZonedDateTime end = ZonedDateTime.now();
    ZonedDateTime start =
        end.minusDays(days - 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    ZonedDateTime prevStart = start.minusDays(days);

    // Fetch orders that were either CLOSED in the period OR are currently OPEN but were opened in
    // the period
    List<Order> orders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
                restaurantId, OrderStatus.CLOSED, start, end));

    List<Order> openOrders =
        orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN);
    orders.addAll(
        openOrders.stream()
            .filter(o -> !o.getOpenedAt().isBefore(start) && !o.getOpenedAt().isAfter(end))
            .toList());

    List<Order> prevOrders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
                restaurantId, OrderStatus.CLOSED, prevStart, start));

    ReportsOverviewResponse response = new ReportsOverviewResponse();
    response.setStats(calculateStats(orders, prevOrders, start, end, prevStart));

    response.setRevenueChart(calculateRevenueChart(orders, start, days));
    response.setOrdersChart(
        calculateMetricChart(orders, start, days, o -> BigDecimal.valueOf(o.getTickets().size())));
    response.setCustomersChart(
        calculateMetricChart(
            orders, start, days, o -> BigDecimal.valueOf(o.getCustomers().size())));

    // Previous periods (set to empty or calculated if we want the comparison line)
    // The user asked "why 2 lines", so I'll keep them but they must be accurate.
    response.setRevenueChartPrevious(calculateRevenueChart(prevOrders, prevStart, days));
    // We need to shift labels of previous chart to match current labels for overlaying
    syncPreviousLabels(response, start, days);

    response.setCategorySales(calculateCategorySales(orders));
    response.setTopItems(calculateTopItems(orders));
    response.setPeakHours(calculatePeakHours(orders));
    response.setPaymentMethods(calculatePaymentMethods(orders));
    response.setPromoUsage(calculatePromoUsage(orders));
    response.setCustomerRatings(calculateRatings(restaurantId, start));
    response.setTableOccupancy(calculateOccupancy(orders, restaurantId));

    return response;
  }

  private void syncPreviousLabels(ReportsOverviewResponse response, ZonedDateTime start, int days) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
    for (int i = 0; i < days; i++) {
      String label = start.plusDays(i).format(formatter);
      if (response.getRevenueChartPrevious() != null
          && i < response.getRevenueChartPrevious().size()) {
        response.getRevenueChartPrevious().get(i).setLabel(label);
      }
    }
  }

  private ReportStats calculateStats(
      List<Order> orders,
      List<Order> prevOrders,
      ZonedDateTime start,
      ZonedDateTime end,
      ZonedDateTime prevStart) {
    // Realized Revenue = Sum of all payments made in the period
    BigDecimal totalRevenue =
        orders.stream()
            .flatMap(o -> o.getPayments().stream())
            .filter(p -> !p.getPaidAt().isBefore(start) && p.getPaidAt().isBefore(end))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal prevRevenue =
        prevOrders.stream()
            .flatMap(o -> o.getPayments().stream())
            .filter(p -> !p.getPaidAt().isBefore(prevStart) && p.getPaidAt().isBefore(start))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    ReportStats stats = new ReportStats();
    stats.setTotalRevenue(totalRevenue.doubleValue());

    // Total Pedidos = Total unique orders that had at least one ticket or payment
    long totalOrdersCount =
        orders.stream()
            .filter(o -> !o.getTickets().isEmpty() || !o.getPayments().isEmpty())
            .count();
    stats.setTotalOrders((int) totalOrdersCount);

    // Total Customers = Sum of participants in those orders
    int totalParticipants =
        orders.stream()
            .filter(o -> !o.getTickets().isEmpty() || !o.getPayments().isEmpty())
            .mapToInt(o -> o.getCustomers().size())
            .sum();
    stats.setTotalCustomers(totalParticipants);

    stats.setAvgTicket(
        totalOrdersCount == 0
            ? 0.0
            : totalRevenue
                .divide(BigDecimal.valueOf(totalOrdersCount), 2, java.math.RoundingMode.HALF_UP)
                .doubleValue());

    stats.setRevenueTrend(calculateTrend(totalRevenue, prevRevenue));

    long prevOrdersCount =
        prevOrders.stream()
            .filter(o -> !o.getTickets().isEmpty() || !o.getPayments().isEmpty())
            .count();
    stats.setOrdersTrend(
        calculateTrend(BigDecimal.valueOf(totalOrdersCount), BigDecimal.valueOf(prevOrdersCount)));

    return stats;
  }

  private double calculateTrend(BigDecimal current, BigDecimal previous) {
    if (previous.compareTo(BigDecimal.ZERO) == 0)
      return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
    return current
        .subtract(previous)
        .divide(previous, 4, java.math.RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .doubleValue();
  }

  private List<ChartDataPoint> calculateRevenueChart(
      List<Order> orders, ZonedDateTime start, int days) {
    Map<String, BigDecimal> dailyValues = new LinkedHashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    for (int i = 0; i < days; i++) {
      dailyValues.put(start.plusDays(i).format(formatter), BigDecimal.ZERO);
    }

    orders.forEach(
        o -> {
          o.getPayments()
              .forEach(
                  p -> {
                    String key = p.getPaidAt().format(formatter);
                    if (dailyValues.containsKey(key)) {
                      dailyValues.merge(key, p.getAmount(), BigDecimal::add);
                    }
                  });
        });

    return dailyValues.entrySet().stream()
        .map(
            e -> {
              ChartDataPoint dp = new ChartDataPoint();
              dp.setLabel(e.getKey());
              dp.setValue(e.getValue().doubleValue());
              return dp;
            })
        .collect(Collectors.toList());
  }

  private List<ChartDataPoint> calculateMetricChart(
      List<Order> orders,
      ZonedDateTime start,
      int days,
      Function<Order, BigDecimal> valueExtractor) {
    Map<String, BigDecimal> dailyValues = new LinkedHashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    for (int i = 0; i < days; i++) {
      dailyValues.put(start.plusDays(i).format(formatter), BigDecimal.ZERO);
    }

    orders.forEach(
        o -> {
          String key =
              (o.getClosedAt() != null ? o.getClosedAt() : o.getOpenedAt()).format(formatter);
          if (dailyValues.containsKey(key)) {
            dailyValues.merge(key, valueExtractor.apply(o), BigDecimal::add);
          }
        });

    return dailyValues.entrySet().stream()
        .map(
            e -> {
              ChartDataPoint dp = new ChartDataPoint();
              dp.setLabel(e.getKey());
              dp.setValue(e.getValue().doubleValue());
              return dp;
            })
        .collect(Collectors.toList());
  }

  private List<CategorySaleResponse> calculateCategorySales(List<Order> orders) {
    Map<String, BigDecimal> salesByCategory = new HashMap<>();
    Map<UUID, Item> itemCache = new HashMap<>();

    orders.stream()
        .flatMap(o -> o.getItems().stream())
        .forEach(
            item -> {
              Item domainItem =
                  itemCache.computeIfAbsent(
                      item.getItemId(), id -> itemRepository.findById(id).orElse(null));
              String catName = "Outros";
              if (domainItem != null && domainItem.getCategory() != null) {
                catName =
                    domainItem
                        .getCategory()
                        .getName()
                        .get(dev.thiagooliveira.tablesplit.domain.common.Language.PT);
              }
              salesByCategory.merge(catName, item.getTotalPrice(), BigDecimal::add);
            });

    BigDecimal total = salesByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    String[] colors = {"#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899", "#06b6d4"};
    int i = 0;

    List<CategorySaleResponse> result = new ArrayList<>();
    for (Map.Entry<String, BigDecimal> entry : salesByCategory.entrySet()) {
      CategorySaleResponse res = new CategorySaleResponse();
      res.setCategoryName(entry.getKey());
      res.setTotalAmount(entry.getValue().doubleValue());
      res.setPercentage(
          total.signum() == 0
              ? 0.0
              : entry
                  .getValue()
                  .multiply(BigDecimal.valueOf(100))
                  .divide(total, 2, java.math.RoundingMode.HALF_UP)
                  .doubleValue());
      res.setColor(colors[i % colors.length]);
      result.add(res);
      i++;
    }

    return result.stream()
        .sorted(Comparator.comparing(CategorySaleResponse::getTotalAmount).reversed())
        .collect(Collectors.toList());
  }

  private List<ItemSaleResponse> calculateTopItems(List<Order> orders) {
    Map<UUID, ItemSaleResponse> itemSales = new HashMap<>();
    Map<UUID, Item> itemCache = new HashMap<>();

    orders.stream()
        .flatMap(o -> o.getItems().stream())
        .forEach(
            item -> {
              ItemSaleResponse res =
                  itemSales.computeIfAbsent(
                      item.getItemId(),
                      id -> {
                        Item domainItem =
                            itemCache.computeIfAbsent(
                                id, itemId -> itemRepository.findById(itemId).orElse(null));
                        ItemSaleResponse r = new ItemSaleResponse();
                        r.setItemId(id);
                        r.setName(
                            item.getName()
                                .get(dev.thiagooliveira.tablesplit.domain.common.Language.PT));
                        r.setQuantity(0);
                        r.setTotalRevenue(0.0);
                        if (domainItem != null) {
                          r.setImageUrl(domainItem.getImage());
                          if (domainItem.getCategory() != null) {
                            r.setCategoryName(
                                domainItem
                                    .getCategory()
                                    .getName()
                                    .get(dev.thiagooliveira.tablesplit.domain.common.Language.PT));
                          }
                        }
                        return r;
                      });
              res.setQuantity(res.getQuantity() + item.getQuantity());
              res.setTotalRevenue(res.getTotalRevenue() + item.getTotalPrice().doubleValue());
            });

    return itemSales.values().stream()
        .sorted(Comparator.comparing(ItemSaleResponse::getQuantity).reversed())
        .limit(5)
        .collect(Collectors.toList());
  }

  private List<PeakHourResponse> calculatePeakHours(List<Order> orders) {
    Map<Integer, Integer> counts = new HashMap<>();
    for (int i = 0; i < 24; i++) counts.put(i, 0);

    orders.forEach(o -> counts.merge(o.getOpenedAt().getHour(), 1, Integer::sum));

    int total = orders.size();
    return counts.entrySet().stream()
        .map(
            e -> {
              PeakHourResponse res = new PeakHourResponse();
              res.setHour(e.getKey());
              res.setOrderCount(e.getValue());
              res.setPercentage(total == 0 ? 0.0 : (double) e.getValue() / total * 100);
              return res;
            })
        .sorted(Comparator.comparing(PeakHourResponse::getHour))
        .collect(Collectors.toList());
  }

  private List<PaymentMethodResponse> calculatePaymentMethods(List<Order> orders) {
    Map<String, PaymentMethodStats> stats = new HashMap<>();

    orders.stream()
        .flatMap(o -> o.getPayments().stream())
        .forEach(
            p -> {
              PaymentMethodStats s =
                  stats.computeIfAbsent(p.getMethod().name(), k -> new PaymentMethodStats());
              s.amount = s.amount.add(p.getAmount());
              s.count++;
            });

    BigDecimal totalAmount =
        stats.values().stream().map(s -> s.amount).reduce(BigDecimal.ZERO, BigDecimal::add);

    return stats.entrySet().stream()
        .map(
            e -> {
              PaymentMethodResponse res = new PaymentMethodResponse();
              res.setMethod(e.getKey());
              res.setTotalAmount(e.getValue().amount.doubleValue());
              res.setCount(e.getValue().count);
              res.setPercentage(
                  totalAmount.signum() == 0
                      ? 0.0
                      : e.getValue()
                          .amount
                          .divide(totalAmount, 4, java.math.RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                          .doubleValue());
              return res;
            })
        .collect(Collectors.toList());
  }

  private List<PromoUsageResponse> calculatePromoUsage(List<Order> orders) {
    Map<UUID, PromoUsageStats> stats = new HashMap<>();

    orders.stream()
        .flatMap(o -> o.getItems().stream())
        .filter(item -> item.getPromotionSnapshot() != null)
        .forEach(
            item -> {
              var snapshot = item.getPromotionSnapshot();
              PromoUsageStats s =
                  stats.computeIfAbsent(
                      snapshot.promotionId(),
                      id -> {
                        PromoUsageStats st = new PromoUsageStats();
                        st.name =
                            promotionRepository
                                .findById(id)
                                .map(Promotion::getName)
                                .orElse("Promoção");
                        return st;
                      });
              s.count += item.getQuantity();
              BigDecimal discountPerUnit = snapshot.originalPrice().subtract(item.getUnitPrice());
              s.totalDiscount =
                  s.totalDiscount.add(
                      discountPerUnit.multiply(BigDecimal.valueOf(item.getQuantity())));
            });

    return stats.entrySet().stream()
        .map(
            e -> {
              PromoUsageResponse res = new PromoUsageResponse();
              res.setPromotionId(e.getKey());
              res.setName(e.getValue().name);
              res.setUsageCount(e.getValue().count);
              res.setTotalDiscount(e.getValue().totalDiscount.doubleValue());
              return res;
            })
        .collect(Collectors.toList());
  }

  private RatingStats calculateRatings(UUID restaurantId, ZonedDateTime since) {
    var distribution = feedbackRepository.getRatingDistribution(restaurantId, since);
    RatingStats stats = new RatingStats();
    stats.setDistribution(
        distribution.entrySet().stream()
            .collect(Collectors.toMap(e -> "r" + e.getKey(), e -> e.getValue().intValue())));

    long total = distribution.values().stream().mapToLong(Long::longValue).sum();
    double sum = distribution.entrySet().stream().mapToDouble(e -> e.getKey() * e.getValue()).sum();

    stats.setTotalReviews((int) total);
    stats.setAverageRating(total == 0 ? 0.0 : sum / total);
    return stats;
  }

  private OccupancyStats calculateOccupancy(List<Order> orders, UUID restaurantId) {
    OccupancyStats stats = new OccupancyStats();
    stats.setTotalTablesAvailable((int) tableRepository.count(restaurantId));

    long totalMinutes = 0;
    int count = 0;
    for (Order o : orders) {
      if (o.getClosedAt() != null && o.getOpenedAt() != null) {
        totalMinutes += java.time.Duration.between(o.getOpenedAt(), o.getClosedAt()).toMinutes();
        count++;
      }
    }
    stats.setAverageDurationMinutes(count == 0 ? 0 : (int) (totalMinutes / count));
    stats.setPeakSimultaneousTables(calculatePeakSimultaneous(orders));

    return stats;
  }

  private int calculatePeakSimultaneous(List<Order> orders) {
    if (orders.isEmpty()) return 0;
    List<Event> events = new ArrayList<>();
    for (Order o : orders) {
      events.add(new Event(o.getOpenedAt(), 1));
      if (o.getClosedAt() != null) events.add(new Event(o.getClosedAt(), -1));
    }
    events.sort(Comparator.comparing(e -> e.time));

    int peak = 0;
    int current = 0;
    for (Event e : events) {
      current += e.type;
      peak = Math.max(peak, current);
    }
    return peak;
  }

  private static class PaymentMethodStats {
    BigDecimal amount = BigDecimal.ZERO;
    int count = 0;
  }

  private static class PromoUsageStats {
    String name;
    int count = 0;
    BigDecimal totalDiscount = BigDecimal.ZERO;
  }

  private record Event(ZonedDateTime time, int type) {}
}

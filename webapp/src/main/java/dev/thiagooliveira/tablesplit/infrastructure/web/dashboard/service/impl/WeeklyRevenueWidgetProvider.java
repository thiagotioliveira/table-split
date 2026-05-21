package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.WeeklyRevenueChartPoint;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.WeeklyRevenueWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WeeklyRevenueWidgetProvider implements WidgetProvider {

  private final OrderRepository orderRepository;
  private final org.springframework.context.MessageSource messageSource;

  public WeeklyRevenueWidgetProvider(
      OrderRepository orderRepository, org.springframework.context.MessageSource messageSource) {
    this.orderRepository = orderRepository;
    this.messageSource = messageSource;
  }

  @Override
  public Module getRequiredModule() {
    return Module.REPORTS;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    ZonedDateTime now = Time.nowZonedDateTime();
    ZonedDateTime todayStart = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

    // Current Week: 7 days including today (from now - 6 days to now)
    ZonedDateTime startCurrent = todayStart.minusDays(6);
    ZonedDateTime endCurrent = now;

    // Previous Week: 7 days before current week (from now - 13 days to now - 7 days)
    ZonedDateTime startPrevious = startCurrent.minusDays(7);
    ZonedDateTime endPrevious = startCurrent;

    // Fetch current period orders
    List<Order> currentOrders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
                restaurantId, OrderStatus.CLOSED, startCurrent, endCurrent));
    List<Order> openOrders =
        orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN);
    currentOrders.addAll(
        openOrders.stream()
            .filter(
                o ->
                    !o.getOpenedAt().isBefore(startCurrent) && !o.getOpenedAt().isAfter(endCurrent))
            .toList());

    // Fetch previous period orders
    List<Order> previousOrders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
                restaurantId, OrderStatus.CLOSED, startPrevious, endPrevious));

    // Daily maps
    Map<String, BigDecimal> dailyValues = new LinkedHashMap<>();
    Map<String, String> keyToLabel = new LinkedHashMap<>();
    java.time.format.DateTimeFormatter dtf =
        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");

    for (int i = 0; i < 7; i++) {
      ZonedDateTime day = startCurrent.plusDays(i);
      String key = day.format(dtf);
      dailyValues.put(key, BigDecimal.ZERO);

      if (i == 6) {
        String todayLabel =
            messageSource.getMessage("dashboard.widget.weekly_revenue.today", null, "Hoje", locale);
        keyToLabel.put(key, todayLabel);
      } else {
        String weekday = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
        // Remove trailing dot if exists (e.g. "seg.") and capitalize first letter
        weekday = weekday.replace(".", "");
        if (weekday.length() > 0) {
          weekday = weekday.substring(0, 1).toUpperCase() + weekday.substring(1);
        }
        keyToLabel.put(key, weekday);
      }
    }

    // Accumulate current week payments
    currentOrders.forEach(
        o -> {
          o.getPayments()
              .forEach(
                  p -> {
                    String key = p.getPaidAt().format(dtf);
                    if (dailyValues.containsKey(key)) {
                      dailyValues.merge(key, p.getAmount(), BigDecimal::add);
                    }
                  });
        });

    // Accumulate previous week payments for total trend comparison
    BigDecimal totalPrevious =
        previousOrders.stream()
            .flatMap(o -> o.getPayments().stream())
            .filter(
                p -> !p.getPaidAt().isBefore(startPrevious) && p.getPaidAt().isBefore(endPrevious))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalCurrent =
        dailyValues.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    double maxVal =
        dailyValues.values().stream().mapToDouble(BigDecimal::doubleValue).max().orElse(0.0);

    List<WeeklyRevenueChartPoint> bars = new ArrayList<>();
    int index = 0;
    for (Map.Entry<String, BigDecimal> entry : dailyValues.entrySet()) {
      WeeklyRevenueChartPoint bar = new WeeklyRevenueChartPoint();
      bar.setLabel(keyToLabel.get(entry.getKey()));
      double val = entry.getValue().doubleValue();
      bar.setValue(val);
      bar.setHighlight(index == 6); // Highlight "Hoje" (today)

      bars.add(bar);
      index++;
    }

    double trendPercent = 0.0;
    WeeklyRevenueWidgetContent.TrendEnum trend = WeeklyRevenueWidgetContent.TrendEnum.NEUTRAL;
    if (totalPrevious.compareTo(BigDecimal.ZERO) > 0) {
      trendPercent =
          totalCurrent
              .subtract(totalPrevious)
              .divide(totalPrevious, 4, java.math.RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100))
              .doubleValue();
      if (trendPercent > 0) {
        trend = WeeklyRevenueWidgetContent.TrendEnum.UP;
      } else if (trendPercent < 0) {
        trend = WeeklyRevenueWidgetContent.TrendEnum.DOWN;
      }
    } else if (totalCurrent.compareTo(BigDecimal.ZERO) > 0) {
      trendPercent = 100.0;
      trend = WeeklyRevenueWidgetContent.TrendEnum.UP;
    }

    String trendTextKey =
        trendPercent >= 0
            ? "dashboard.widget.weekly_revenue.trend.up"
            : "dashboard.widget.weekly_revenue.trend.down";
    String trendText =
        messageSource.getMessage(
            trendTextKey,
            new Object[] {Math.abs(trendPercent)},
            trendPercent >= 0
                ? String.format("+%.0f%% vs semana passada", trendPercent)
                : String.format("%.0f%% vs semana passada", trendPercent),
            locale);

    String currencySymbol = context.getRestaurant().getCurrency().getSymbol();
    String formattedTotal =
        String.format("%s %,.2f", currencySymbol, totalCurrent.doubleValue())
            .replace(",", "X")
            .replace(".", ",")
            .replace("X", ".");

    WeeklyRevenueWidgetContent content = new WeeklyRevenueWidgetContent();
    content.setChartData(bars);
    content.setTotalAmount(formattedTotal);
    content.setTrend(trend);
    content.setChange(trendText);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("weekly_revenue");
    widget.setType(DashboardWidgetResponse.TypeEnum.WEEKLY_REVENUE);
    widget.setTitle(
        messageSource.getMessage(
            "dashboard.widget.weekly_revenue.title", null, "Faturamento Semanal", locale));
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(12);
    widget.setWeeklyRevenueContent(content);

    return widget;
  }
}

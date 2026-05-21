package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.StatWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class BillingWidgetProvider implements WidgetProvider {

  private final OrderRepository orderRepository;

  public BillingWidgetProvider(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
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
    ZonedDateTime yesterdayStart = todayStart.minusDays(1);

    // Today's orders
    List<Order> todayOrders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
                restaurantId, OrderStatus.CLOSED, todayStart, now));
    List<Order> openOrders =
        orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN);
    todayOrders.addAll(
        openOrders.stream()
            .filter(o -> !o.getOpenedAt().isBefore(todayStart) && !o.getOpenedAt().isAfter(now))
            .toList());

    // Yesterday's orders
    List<Order> yesterdayOrders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
                restaurantId, OrderStatus.CLOSED, yesterdayStart, todayStart));

    // Revenues
    BigDecimal todayRevenue =
        todayOrders.stream()
            .flatMap(o -> o.getPayments().stream())
            .filter(p -> !p.getPaidAt().isBefore(todayStart) && p.getPaidAt().isBefore(now))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal yesterdayRevenue =
        yesterdayOrders.stream()
            .flatMap(o -> o.getPayments().stream())
            .filter(
                p -> !p.getPaidAt().isBefore(yesterdayStart) && p.getPaidAt().isBefore(todayStart))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    double trendPercent = 0.0;
    StatWidgetContent.TrendEnum trend = StatWidgetContent.TrendEnum.NEUTRAL;
    if (yesterdayRevenue.compareTo(BigDecimal.ZERO) > 0) {
      trendPercent =
          todayRevenue
              .subtract(yesterdayRevenue)
              .divide(yesterdayRevenue, 4, java.math.RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100))
              .doubleValue();
      if (trendPercent > 0) {
        trend = StatWidgetContent.TrendEnum.UP;
      } else if (trendPercent < 0) {
        trend = StatWidgetContent.TrendEnum.DOWN;
      }
    } else if (todayRevenue.compareTo(BigDecimal.ZERO) > 0) {
      trendPercent = 100.0;
      trend = StatWidgetContent.TrendEnum.UP;
    }

    String currencySymbol = context.getRestaurant().getCurrency().getSymbol();
    String formattedValue =
        String.format("%s %,.2f", currencySymbol, todayRevenue.doubleValue())
            .replace(",", "X")
            .replace(".", ",")
            .replace("X", ".");

    String trendText =
        trendPercent >= 0
            ? String.format("+%.0f%%", trendPercent)
            : String.format("%.0f%%", trendPercent);

    StatWidgetContent content = new StatWidgetContent();
    content.setValue(formattedValue);
    content.setChange(trendText);
    content.setTrend(trend);
    content.setComparisonLabel("em relação a ontem");

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("billing");
    widget.setType(DashboardWidgetResponse.TypeEnum.STAT);
    widget.setTitle("Faturamento Hoje");
    widget.setSize(DashboardWidgetResponse.SizeEnum.SMALL);
    widget.setIcon(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M12 2v20\"/><path d=\"M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6\"/></svg>");
    widget.setOrder(1);
    widget.setStatContent(content);

    return widget;
  }
}

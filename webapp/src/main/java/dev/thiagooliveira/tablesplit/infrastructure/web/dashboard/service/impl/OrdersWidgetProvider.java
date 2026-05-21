package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.StatWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class OrdersWidgetProvider implements WidgetProvider {

  private final OrderRepository orderRepository;

  public OrdersWidgetProvider(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public Module getRequiredModule() {
    return Module.ORDERS;
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

    long todayCount =
        todayOrders.stream()
            .filter(o -> !o.getTickets().isEmpty() || !o.getPayments().isEmpty())
            .count();

    long yesterdayCount =
        yesterdayOrders.stream()
            .filter(o -> !o.getTickets().isEmpty() || !o.getPayments().isEmpty())
            .count();

    double trendPercent = 0.0;
    StatWidgetContent.TrendEnum trend = StatWidgetContent.TrendEnum.NEUTRAL;
    if (yesterdayCount > 0) {
      trendPercent = ((double) (todayCount - yesterdayCount) / yesterdayCount) * 100.0;
      if (trendPercent > 0) {
        trend = StatWidgetContent.TrendEnum.UP;
      } else if (trendPercent < 0) {
        trend = StatWidgetContent.TrendEnum.DOWN;
      }
    } else if (todayCount > 0) {
      trendPercent = 100.0;
      trend = StatWidgetContent.TrendEnum.UP;
    }

    String trendText =
        trendPercent >= 0
            ? String.format("+%.0f%%", trendPercent)
            : String.format("%.0f%%", trendPercent);

    StatWidgetContent content = new StatWidgetContent();
    content.setValue(String.valueOf(todayCount));
    content.setChange(trendText);
    content.setTrend(trend);
    content.setComparisonLabel("em relação a ontem");

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("orders");
    widget.setType(DashboardWidgetResponse.TypeEnum.STAT);
    widget.setTitle("Pedidos Hoje");
    widget.setSize(DashboardWidgetResponse.SizeEnum.SMALL);
    widget.setIcon(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2\"/><path d=\"M15 2H9a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1Z\"/></svg>");
    widget.setOrder(2);
    widget.setStatContent(content);

    return widget;
  }
}

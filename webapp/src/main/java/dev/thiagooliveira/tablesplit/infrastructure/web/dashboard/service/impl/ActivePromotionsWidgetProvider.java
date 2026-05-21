package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.ListWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.ListWidgetItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ActivePromotionsWidgetProvider implements WidgetProvider {

  private final PromotionRepository promotionRepository;
  private final OrderRepository orderRepository;

  public ActivePromotionsWidgetProvider(
      PromotionRepository promotionRepository, OrderRepository orderRepository) {
    this.promotionRepository = promotionRepository;
    this.orderRepository = orderRepository;
  }

  @Override
  public Module getRequiredModule() {
    return Module.PROMOTIONS;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    List<Promotion> promotions = promotionRepository.findByRestaurantId(restaurantId);
    List<Promotion> activePromotions = promotions.stream().filter(Promotion::isActive).toList();

    ZonedDateTime now = Time.nowZonedDateTime();
    ZonedDateTime todayStart = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

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

    Map<UUID, Integer> promoUsages = new HashMap<>();
    todayOrders.stream()
        .flatMap(o -> o.getItems().stream())
        .filter(item -> item.getPromotionSnapshot() != null)
        .forEach(
            item -> {
              UUID promoId = item.getPromotionSnapshot().promotionId();
              promoUsages.merge(promoId, item.getQuantity(), Integer::sum);
            });

    List<ListWidgetItem> items = new ArrayList<>();
    String currencySymbol = context.getRestaurant().getCurrency().getSymbol();

    for (Promotion promo : activePromotions) {
      ListWidgetItem item = new ListWidgetItem();
      item.setTitle(promo.getName());

      String discountText = "";
      if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
        discountText = String.format("-%.0f%%", promo.getDiscountValue().doubleValue());
      } else if (promo.getDiscountType() == DiscountType.FIXED_VALUE) {
        discountText =
            String.format("-%s %.2f", currencySymbol, promo.getDiscountValue().doubleValue());
      } else {
        discountText = "Brinde";
      }
      item.setBadge(discountText);

      String metaText = promo.getDescription();
      if (metaText == null || metaText.isEmpty()) {
        metaText = "Sem descrição";
      }
      item.setMeta(metaText);

      int usages = promoUsages.getOrDefault(promo.getId(), 0);
      item.setValue(usages + (usages == 1 ? " uso hoje" : " usos hoje"));
      item.setValueColor("green");

      items.add(item);
    }

    ListWidgetContent content = new ListWidgetContent();
    content.setItems(items);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("active_promotions");
    widget.setType(DashboardWidgetResponse.TypeEnum.LIST);
    widget.setTitle("Promoções Ativas");
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(8);
    widget.setListContent(content);

    return widget;
  }
}

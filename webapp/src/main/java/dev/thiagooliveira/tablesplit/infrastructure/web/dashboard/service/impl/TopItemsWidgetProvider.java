package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TopItemsWidgetProvider implements WidgetProvider {

  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;

  public TopItemsWidgetProvider(OrderRepository orderRepository, ItemRepository itemRepository) {
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
  }

  @Override
  public Module getRequiredModule() {
    return Module.MENU;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
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

    Map<UUID, Integer> itemQuantities = new HashMap<>();
    todayOrders.stream()
        .flatMap(o -> o.getItems().stream())
        .forEach(
            item -> {
              itemQuantities.merge(item.getItemId(), item.getQuantity(), Integer::sum);
            });

    List<UUID> topItemIds =
        itemQuantities.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .toList();

    Map<UUID, Item> itemMap =
        itemRepository.findAllById(topItemIds).stream()
            .collect(Collectors.toMap(Item::getId, Function.identity(), (a, b) -> a));

    List<ListWidgetItem> items = new ArrayList<>();
    dev.thiagooliveira.tablesplit.domain.common.Language lang =
        "en".equals(locale.getLanguage())
            ? dev.thiagooliveira.tablesplit.domain.common.Language.EN
            : dev.thiagooliveira.tablesplit.domain.common.Language.PT;

    for (UUID itemId : topItemIds) {
      Item itemDomain = itemMap.get(itemId);
      if (itemDomain == null) continue;

      ListWidgetItem item = new ListWidgetItem();
      item.setTitle(itemDomain.getName().get(lang));

      String categoryName = "";
      if (itemDomain.getCategory() != null) {
        categoryName = itemDomain.getCategory().getName().get(lang);
      } else {
        categoryName = "Geral";
      }
      item.setMeta(categoryName);

      int qty = itemQuantities.getOrDefault(itemId, 0);
      item.setValue(qty + "x");

      if (itemDomain.getImage() != null && !itemDomain.getImage().isEmpty()) {
        item.setImageUrl(itemDomain.getImage());
      }

      items.add(item);
    }

    ListWidgetContent content = new ListWidgetContent();
    content.setItems(items);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("top_items");
    widget.setType(DashboardWidgetResponse.TypeEnum.LIST);
    widget.setTitle("Mais Vendidos Hoje");
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(9);
    widget.setListContent(content);

    return widget;
  }
}

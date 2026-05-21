package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.ListWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.ListWidgetItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class RecentItemsWidgetProvider implements WidgetProvider {

  private final GetItem getItem;

  public RecentItemsWidgetProvider(GetItem getItem) {
    this.getItem = getItem;
  }

  @Override
  public Module getRequiredModule() {
    return Module.MENU;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    dev.thiagooliveira.tablesplit.domain.common.Language lang =
        "en".equals(locale.getLanguage())
            ? dev.thiagooliveira.tablesplit.domain.common.Language.EN
            : dev.thiagooliveira.tablesplit.domain.common.Language.PT;

    List<Item> allItems = getItem.execute(restaurantId, List.of(lang));

    // Limit to 5
    List<Item> recentItems = allItems.stream().limit(5).toList();

    List<ListWidgetItem> items = new ArrayList<>();
    String currencySymbol = context.getRestaurant().getCurrency().getSymbol();

    for (Item itemDomain : recentItems) {
      ListWidgetItem item = new ListWidgetItem();
      item.setTitle(itemDomain.getName().get(lang));

      String categoryName = "";
      if (itemDomain.getCategory() != null) {
        categoryName = itemDomain.getCategory().getName().get(lang);
      } else {
        categoryName = "Geral";
      }
      item.setMeta(categoryName);

      String formattedPrice =
          String.format("%s %,.2f", currencySymbol, itemDomain.getPrice().doubleValue())
              .replace(",", "X")
              .replace(".", ",")
              .replace("X", ".");
      item.setValue(formattedPrice);

      if (itemDomain.getImage() != null && !itemDomain.getImage().isEmpty()) {
        item.setImageUrl(itemDomain.getImage());
      }

      items.add(item);
    }

    ListWidgetContent content = new ListWidgetContent();
    content.setItems(items);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("recent_items");
    widget.setType(DashboardWidgetResponse.TypeEnum.LIST);
    widget.setTitle("Itens Recentes");
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(10);
    widget.setListContent(content);

    return widget;
  }
}

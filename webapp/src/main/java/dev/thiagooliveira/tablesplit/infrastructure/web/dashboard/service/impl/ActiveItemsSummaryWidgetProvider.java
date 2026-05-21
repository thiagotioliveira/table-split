package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.StatWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ActiveItemsSummaryWidgetProvider implements WidgetProvider {

  private final GetItem getItem;

  public ActiveItemsSummaryWidgetProvider(GetItem getItem) {
    this.getItem = getItem;
  }

  @Override
  public Module getRequiredModule() {
    return Module.MENU;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    long activeCount = getItem.countActive(restaurantId);

    StatWidgetContent content = new StatWidgetContent();
    content.setValue(String.valueOf(activeCount));
    content.setColor("amber");

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("active_items_summary");
    widget.setType(DashboardWidgetResponse.TypeEnum.STAT);
    widget.setTitle("Ativos");
    widget.setSize(DashboardWidgetResponse.SizeEnum.SMALL);
    widget.setOrder(5);
    widget.setStatContent(content);

    return widget;
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.StatWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class CategoriesSummaryWidgetProvider implements WidgetProvider {

  private final GetCategory getCategory;

  public CategoriesSummaryWidgetProvider(GetCategory getCategory) {
    this.getCategory = getCategory;
  }

  @Override
  public Module getRequiredModule() {
    return Module.MENU;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    long totalCount = getCategory.count(restaurantId);

    StatWidgetContent content = new StatWidgetContent();
    content.setValue(String.valueOf(totalCount));
    content.setColor("blue");

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("categories_summary");
    widget.setType(DashboardWidgetResponse.TypeEnum.STAT);
    widget.setTitle("Categorias");
    widget.setSize(DashboardWidgetResponse.SizeEnum.SMALL);
    widget.setOrder(3);
    widget.setStatContent(content);

    return widget;
  }
}

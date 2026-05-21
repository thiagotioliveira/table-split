package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.GridStatusItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.GridStatusWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class TableStatusWidgetProvider implements WidgetProvider {

  private final TableRepository tableRepository;

  public TableStatusWidgetProvider(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  @Override
  public Module getRequiredModule() {
    return Module.TABLES;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    List<Table> tables = tableRepository.findAllByRestaurantId(restaurantId);

    // Sort tables naturally by code (e.g. numeric sort or alphabetical sort)
    tables.sort(
        (t1, t2) -> {
          try {
            int n1 = Integer.parseInt(t1.getCod());
            int n2 = Integer.parseInt(t2.getCod());
            return Integer.compare(n1, n2);
          } catch (NumberFormatException e) {
            return t1.getCod().compareTo(t2.getCod());
          }
        });

    List<GridStatusItem> items = new ArrayList<>();
    for (Table table : tables) {
      GridStatusItem item = new GridStatusItem();
      item.setCode(table.getCod());

      GridStatusItem.StatusEnum statusEnum = GridStatusItem.StatusEnum.AVAILABLE;
      if (table.getStatus() == TableStatus.OCCUPIED) {
        statusEnum = GridStatusItem.StatusEnum.OCCUPIED;
      } else if (table.getStatus() == TableStatus.WAITING) {
        statusEnum = GridStatusItem.StatusEnum.WAITING;
      }
      item.setStatus(statusEnum);
      items.add(item);
    }

    GridStatusWidgetContent content = new GridStatusWidgetContent();
    content.setItems(items);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("table_status");
    widget.setType(DashboardWidgetResponse.TypeEnum.GRID_STATUS);
    widget.setTitle("Status das Mesas");
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(7);
    widget.setGridStatusContent(content);

    return widget;
  }
}

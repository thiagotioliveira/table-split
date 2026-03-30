package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CustomerMenuModel;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CustomerTableController {

  private final GetRestaurant getRestaurant;
  private final GetTables getTables;
  private final GetCategory getCategory;
  private final GetItem getItem;
  private final OpenTable openTable;
  private final TransactionalContext transactionalContext;

  public CustomerTableController(
      GetRestaurant getRestaurant,
      GetTables getTables,
      GetCategory getCategory,
      GetItem getItem,
      OpenTable openTable,
      TransactionalContext transactionalContext) {
    this.getRestaurant = getRestaurant;
    this.getTables = getTables;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.openTable = openTable;
    this.transactionalContext = transactionalContext;
  }

  private Table getAndOpenTable(Restaurant restaurant, String tableCode) {
    var table = getTables.findByRestaurantIdAndCod(restaurant.getId(), tableCode).orElseThrow();
    if (table.getStatus() == TableStatus.AVAILABLE) {
      final java.util.UUID tableId = table.getId();
      transactionalContext.execute(() -> openTable.execute(tableId, restaurant.getServiceFee()));
      table = getTables.findById(tableId).orElse(table);
    }
    return table;
  }

  @GetMapping("/@{slug}/table/{tableCode}")
  public String index(@PathVariable String slug, @PathVariable String tableCode, Model model) {
    var restaurant = getRestaurant.execute(slug).orElseThrow();
    var table = getAndOpenTable(restaurant, tableCode);

    String cuisineType = null;
    if (restaurant.getCuisineType() != null) {
      cuisineType =
          dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType.valueOf(
                  restaurant.getCuisineType().name())
              .getLabel();
    }

    model.addAttribute("restaurant", restaurant);
    model.addAttribute("table", table);
    model.addAttribute("cuisineType", cuisineType);
    return "table-entry";
  }

  @GetMapping("/@{slug}/table/{tableCode}/menu")
  public String menu(
      @PathVariable String slug, @PathVariable String tableCode, Model model, Locale locale) {
    var restaurant = getRestaurant.execute(slug).orElseThrow();
    var table = getAndOpenTable(restaurant, tableCode);
    var requestLanguages = java.util.List.of(Language.fromLocale(locale));
    var categories = getCategory.execute(restaurant.getId(), requestLanguages);
    var items = getItem.execute(restaurant.getId(), requestLanguages, true);

    CustomerMenuModel menuModel = new CustomerMenuModel(restaurant, categories, items);
    model.addAttribute("customerMenu", menuModel);
    model.addAttribute("table", table); // used to toggle table-specific UI

    return "customer-menu";
  }
}

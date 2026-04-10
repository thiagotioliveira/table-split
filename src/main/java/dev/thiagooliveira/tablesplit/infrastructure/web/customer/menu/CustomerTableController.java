package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.application.order.model.*;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CustomerMenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.OrderCustomerModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class CustomerTableController {

  private final GetRestaurant getRestaurant;
  private final GetTables getTables;
  private final GetCategory getCategory;
  private final GetItem getItem;
  private final OpenTable openTable;
  private final PlaceOrder placeOrder;
  private final GetOrder getOrder;
  private final UpdateCustomerName updateCustomerName;
  private final CallWaiter callWaiter;
  private final TransactionalContext transactionalContext;

  public CustomerTableController(
      GetRestaurant getRestaurant,
      GetTables getTables,
      GetCategory getCategory,
      GetItem getItem,
      OpenTable openTable,
      PlaceOrder placeOrder,
      GetOrder getOrder,
      UpdateCustomerName updateCustomerName,
      CallWaiter callWaiter,
      TransactionalContext transactionalContext) {
    this.getRestaurant = getRestaurant;
    this.getTables = getTables;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.openTable = openTable;
    this.placeOrder = placeOrder;
    this.getOrder = getOrder;
    this.updateCustomerName = updateCustomerName;
    this.callWaiter = callWaiter;
    this.transactionalContext = transactionalContext;
  }

  @PostMapping("/@{slug}/table/{tableCode}/customer-name")
  @ResponseBody
  public ResponseEntity<Void> updateCustomerName(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody UpdateCustomerNameRequest request) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    var table = getTable(restaurant, tableCode);

    transactionalContext.execute(
        () -> updateCustomerName.execute(table.getId(), request.customerId(), request.name()));

    return ResponseEntity.ok().build();
  }

  private Table getTable(Restaurant restaurant, String tableCode) {
    return getTables
        .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
        .orElseThrow(() -> new NotFoundException("error.table.not.found"));
  }

  private Table getAndOpenTable(
      Restaurant restaurant, String tableCode, UUID customerId, String customerName) {
    var table =
        getTables
            .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
            .orElseThrow(() -> new NotFoundException("error.table.not.found"));
    if (table.getStatus() == TableStatus.AVAILABLE) {
      final java.util.UUID tableId = table.getId();
      transactionalContext.execute(
          () -> openTable.execute(tableId, restaurant.getServiceFee(), customerId, customerName));
      table = getTables.findById(tableId).orElse(table);
    }
    return table;
  }

  @GetMapping("/@{slug}/table/{tableCode}")
  public String index(@PathVariable String slug, @PathVariable String tableCode, Model model) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    var table = getTable(restaurant, tableCode);

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
    model.addAttribute("itemTags", ItemTag.values());

    if (!table.isAvailable()) {
      getOrder
          .execute(table.getId())
          .ifPresent(
              order ->
                  model.addAttribute(
                      "tableCustomers",
                      order.getCustomers().stream().map(OrderCustomerModel::new).toList()));
    }
    return "table-entry";
  }

  @GetMapping("/@{slug}/table/{tableCode}/menu")
  public String menu(
      @PathVariable String slug, @PathVariable String tableCode, Model model, Locale locale) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    var table = getTable(restaurant, tableCode);
    if (table.isAvailable()) {
      return String.format("redirect:/@%s/table/%s", slug, tableCode);
    }
    var requestLanguages = java.util.List.of(Language.fromLocale(locale));
    var categories = getCategory.execute(restaurant.getId(), requestLanguages);
    var items = getItem.execute(restaurant.getId(), requestLanguages, true);
    var activeOrder = getOrder.execute(table.getId()).orElse(null);

    CustomerMenuModel menuModel =
        new CustomerMenuModel(restaurant, categories, items, table, activeOrder);
    model.addAttribute("customerMenu", menuModel);
    model.addAttribute("itemTags", ItemTag.values());

    return "customer-menu";
  }

  @PostMapping("/@{slug}/table/{tableCode}/open")
  @ResponseBody
  public ResponseEntity<Void> openTable(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody OpenTableRequest request) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    var table =
        getTables
            .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
            .orElseThrow(() -> new NotFoundException("error.table.not.found"));

    transactionalContext.execute(
        () ->
            openTable.execute(
                table.getId(),
                restaurant.getServiceFee(),
                request.customerId(),
                request.customerName()));

    return ResponseEntity.ok().build();
  }

  @PostMapping("/@{slug}/table/{tableCode}/menu/order")
  @ResponseBody
  public ResponseEntity<Void> placeOrder(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody PlaceOrderRequest request) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    request.setRestaurantId(restaurant.getId());
    request.setTableCod(tableCode);
    request.setServiceFee(restaurant.getServiceFee());

    transactionalContext.execute(() -> placeOrder.execute(request));

    return ResponseEntity.ok().build();
  }

  @PostMapping("/@{slug}/table/{tableCode}/waiter/call")
  @ResponseBody
  public ResponseEntity<Void> callWaiter(
      @PathVariable String slug, @PathVariable String tableCode) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));

    transactionalContext.execute(() -> callWaiter.execute(restaurant.getId(), tableCode));

    return ResponseEntity.ok().build();
  }

  public record OpenTableRequest(java.util.UUID customerId, String customerName) {}
}

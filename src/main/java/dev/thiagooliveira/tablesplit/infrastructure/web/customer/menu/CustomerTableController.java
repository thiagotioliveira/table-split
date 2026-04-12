package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.application.order.model.*;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CustomerMenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.OrderCustomerModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.PaymentModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.TableSummaryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final RateItem rateItem;
  private final SubmitGeneralFeedback submitGeneralFeedback;
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
      RateItem rateItem,
      SubmitGeneralFeedback submitGeneralFeedback,
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
    this.rateItem = rateItem;
    this.submitGeneralFeedback = submitGeneralFeedback;
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

    try {
      transactionalContext.execute(
          () -> updateCustomerName.execute(table.getId(), request.customerId(), request.name()));
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).build();
    }
  }

  private Table getTable(Restaurant restaurant, String tableCode) {
    return getTables
        .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
        .orElseThrow(() -> new NotFoundException("error.table.not.found"));
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
              order -> {
                List<OrderCustomerModel> customers =
                    order.getCustomers().stream()
                        .map(
                            c ->
                                new OrderCustomerModel(
                                    c, order.calculateSubtotalByCustomer(c.getId())))
                        .collect(Collectors.toList());
                model.addAttribute("tableCustomers", customers);
              });
    }
    return "table-entry";
  }

  @GetMapping("/@{slug}/table/{tableCode}/menu")
  public String menu(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @CookieValue(value = "ts_last_order_id", required = false) String lastOrderId,
      @CookieValue(value = "ts_customer_id", required = false) String customerId,
      jakarta.servlet.http.HttpServletResponse response,
      Model model,
      Locale locale) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    var table = getTable(restaurant, tableCode);
    var activeOrder = getOrder.execute(table.getId()).orElse(null);

    // If no active order on table, try to find the last one from cookie
    if (activeOrder == null && lastOrderId != null && !lastOrderId.isEmpty()) {
      try {
        activeOrder = getOrder.findById(UUID.fromString(lastOrderId)).orElse(null);
        // Security check: ensure this order belongs to this table and restaurant
        if (activeOrder != null
            && (!activeOrder.getTableId().equals(table.getId())
                || !activeOrder.getRestaurantId().equals(restaurant.getId()))) {
          activeOrder = null;
        }
      } catch (Exception e) {
        activeOrder = null;
      }
    }

    if (activeOrder == null) {
      if (table.isAvailable()) {
        return String.format("redirect:/@%s/table/%s", slug, tableCode);
      }
      return String.format("redirect:/@%s/table/%s", slug, tableCode); // Fallback to entry
    }

    // Set or refresh cookie for current order
    var orderCookie =
        new jakarta.servlet.http.Cookie("ts_last_order_id", activeOrder.getId().toString());
    orderCookie.setPath("/");
    orderCookie.setMaxAge(60 * 60 * 24); // 24 hours
    response.addCookie(orderCookie);

    var requestLanguages = java.util.List.of(Language.fromLocale(locale));
    var categories = getCategory.execute(restaurant.getId(), requestLanguages);
    var items = getItem.execute(restaurant.getId(), requestLanguages, true);

    CustomerMenuModel menuModel =
        new CustomerMenuModel(restaurant, categories, items, table, activeOrder);

    // Server-side feedback check
    if (customerId != null && !customerId.isEmpty()) {
      try {
        boolean hasSent = rateItem.hasFeedback(activeOrder.getId(), UUID.fromString(customerId));
        if (hasSent) {
          return String.format("redirect:/@%s/table/%s", slug, tableCode);
        }
      } catch (Exception e) {
        // Ignore check errors
      }
    }

    model.addAttribute("customerMenu", menuModel);
    model.addAttribute("itemTags", ItemTag.values());

    return "customer-menu";
  }

  @GetMapping("/@{slug}/table/{tableCode}/menu/data")
  @ResponseBody
  public ResponseEntity<TableDataResponse> getData(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @CookieValue(value = "ts_last_order_id", required = false) String lastOrderId,
      @CookieValue(value = "ts_customer_id", required = false) String customerId,
      Locale locale) {
    try {
      var restaurant =
          getRestaurant
              .execute(slug)
              .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
      var table = getTable(restaurant, tableCode);
      var activeOrder = getOrder.execute(table.getId()).orElse(null);

      if (activeOrder == null && lastOrderId != null && !lastOrderId.isEmpty()) {
        try {
          activeOrder = getOrder.findById(UUID.fromString(lastOrderId)).orElse(null);
          if (activeOrder != null
              && (!activeOrder.getTableId().equals(table.getId())
                  || !activeOrder.getRestaurantId().equals(restaurant.getId()))) {
            activeOrder = null;
          }
        } catch (Exception e) {
          activeOrder = null;
        }
      }

      boolean hasSentFeedback = false;
      final var finalOrder = activeOrder;
      if (customerId != null && !customerId.isEmpty() && finalOrder != null) {
        try {
          hasSentFeedback = rateItem.hasFeedback(finalOrder.getId(), UUID.fromString(customerId));
        } catch (Exception e) {
        }
      }

      final var order = finalOrder;
      var customers =
          order != null
              ? order.getCustomers().stream()
                  .map(c -> new OrderCustomerModel(c, order.calculateSubtotalByCustomer(c.getId())))
                  .collect(Collectors.toList())
              : List.<OrderCustomerModel>of();
      var payments =
          order != null
              ? order.getPayments().stream().map(PaymentModel::new).toList()
              : List.<PaymentModel>of();
      var ticketItems = new ArrayList<SimpleTicketItem>();

      if (finalOrder != null) {
        finalOrder
            .getTickets()
            .forEach(
                t ->
                    t.getItems()
                        .forEach(
                            item -> {
                              var lang =
                                  dev.thiagooliveira.tablesplit.domain.common.Language.fromLocale(
                                      locale);

                              ticketItems.add(
                                  SimpleTicketItem.fromDomain(
                                      item,
                                      finalOrder.getCustomerName(item.getCustomerId()),
                                      t.getCreatedAt(),
                                      lang));
                            }));
      }

      var responseData =
          new TableDataResponse(
              ticketItems,
              customers,
              payments,
              order != null ? order.getId() : null,
              order != null
                  && order.getStatus()
                      != dev.thiagooliveira.tablesplit.domain.order.OrderStatus.OPEN,
              hasSentFeedback,
              order != null ? new TableSummaryModel(order) : null);

      return ResponseEntity.ok(responseData);
    } catch (Exception e) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
    }
  }

  public record TableDataResponse(
      List<SimpleTicketItem> ticketItems,
      List<OrderCustomerModel> customers,
      List<PaymentModel> payments,
      java.util.UUID orderId,
      boolean reviewMode,
      boolean hasSentFeedback,
      TableSummaryModel tableSummary) {}

  public record SimpleTicketItem(
      String id,
      String name,
      int quantity,
      java.math.BigDecimal totalPrice,
      String status,
      String statusLabel,
      String customerId,
      String customerName,
      java.time.ZonedDateTime createdAt,
      Integer rating) {
    public static SimpleTicketItem fromDomain(
        dev.thiagooliveira.tablesplit.domain.order.TicketItem item,
        String customerName,
        java.time.ZonedDateTime createdAt,
        dev.thiagooliveira.tablesplit.domain.common.Language lang) {

      return new SimpleTicketItem(
          item.getId().toString(),
          item.getName().get(lang),
          item.getQuantity(),
          item.getTotalPrice(),
          item.getStatus().name(),
          item.getStatus().getLabel(),
          item.getCustomerId().toString(),
          customerName,
          createdAt,
          item.getRating());
    }
  }

  @PostMapping("/@{slug}/table/{tableCode}/open")
  @ResponseBody
  public ResponseEntity<Void> openTable(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody OpenTableRequest request,
      jakarta.servlet.http.HttpServletResponse response) {
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

    // Persist customerId in cookie for future feedback checks
    var cookie = new jakarta.servlet.http.Cookie("ts_customer_id", request.customerId().toString());
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
    response.addCookie(cookie);

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
      @PathVariable String slug,
      @PathVariable String tableCode,
      @CookieValue(value = "ts_customer_id", required = false) String customerIdStr) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));

    java.util.UUID customerId = null;
    if (customerIdStr != null && !customerIdStr.isBlank()) {
      try {
        customerId = java.util.UUID.fromString(customerIdStr);
      } catch (IllegalArgumentException e) {
        // Ignore invalid UUID
      }
    }

    final java.util.UUID finalCustomerId = customerId;
    transactionalContext.execute(
        () -> callWaiter.execute(restaurant.getId(), tableCode, finalCustomerId));

    return ResponseEntity.ok().build();
  }

  @PostMapping("/@{slug}/table/{tableCode}/feedback/item")
  @ResponseBody
  public ResponseEntity<Void> rateItem(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody RateItemRequest request) {
    transactionalContext.execute(() -> rateItem.execute(request.itemId(), request.rating()));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/@{slug}/table/{tableCode}/feedback/general")
  @ResponseBody
  public ResponseEntity<Void> submitGeneralFeedback(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody GeneralFeedbackRequest request) {
    transactionalContext.execute(
        () ->
            submitGeneralFeedback.execute(
                request.orderId(), request.customerId(), request.rating(), request.comment()));
    return ResponseEntity.ok().build();
  }

  public record RateItemRequest(java.util.UUID itemId, Integer rating) {}

  public record GeneralFeedbackRequest(
      java.util.UUID orderId, java.util.UUID customerId, Integer rating, String comment) {}

  public record OpenTableRequest(java.util.UUID customerId, String customerName) {}

  @ExceptionHandler(
      dev.thiagooliveira.tablesplit.application.order.TableSessionClosedException.class)
  public ResponseEntity<String> handleSessionClosed(
      dev.thiagooliveira.tablesplit.application.order.TableSessionClosedException ex) {
    return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT)
        .body(ex.getMessage());
  }
}

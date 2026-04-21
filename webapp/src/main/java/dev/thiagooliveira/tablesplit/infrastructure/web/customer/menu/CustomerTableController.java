package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.account.GetAccount;
import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.application.order.model.*;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.*;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile.model.ProfileModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
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
  private final MessageSource messageSource;
  private final GetAccount getAccount;

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
      TransactionalContext transactionalContext,
      MessageSource messageSource,
      GetAccount getAccount) {
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
    this.messageSource = messageSource;
    this.getAccount = getAccount;
  }

  @PostMapping("/@{slug}/table/{tableCode}/customer-name")
  @ResponseBody
  public ResponseEntity<Void> updateCustomerName(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody UpdateCustomerNameRequest request) {
    var restaurant = getRestaurantBySlug(slug);
    checkPlan(restaurant);
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
  public String index(
      @PathVariable String slug, @PathVariable String tableCode, Locale locale, Model model) {
    var restaurant = getRestaurantBySlug(slug);
    if (isStarter(restaurant)) {
      return redirectToMenu(restaurant);
    }
    getTable(restaurant, tableCode);

    model.addAttribute(
        "profile",
        new ProfileModel(
            restaurant,
            dev.thiagooliveira.tablesplit.infrastructure.utils.Time.getZoneId(),
            messageSource));

    model.addAttribute("tableCode", tableCode);
    model.addAttribute("slug", slug);

    return "restaurant-profile";
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
    var restaurant = getRestaurantBySlug(slug);
    if (isStarter(restaurant)) {
      return redirectToMenu(restaurant);
    }
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

    boolean isNewCustomer = customerId == null || customerId.isEmpty();

    if (activeOrder == null || isNewCustomer) {
      var domainLanguages = java.util.List.of(Language.fromLocale(locale));
      var categories = getCategory.execute(restaurant.getId(), domainLanguages);
      var items = getItem.execute(restaurant.getId(), domainLanguages, true);
      CustomerMenuModel menuModel =
          new CustomerMenuModel(
              restaurant,
              categories,
              items,
              table,
              activeOrder,
              dev.thiagooliveira.tablesplit.infrastructure.utils.Time.getZoneId(),
              messageSource);

      List<OrderCustomerModel> tableCustomers = null;
      if (activeOrder != null) {
        final var finalActiveOrder = activeOrder;
        tableCustomers =
            activeOrder.getCustomers().stream()
                .map(
                    c ->
                        new OrderCustomerModel(
                            c, finalActiveOrder.calculateSubtotalByCustomer(c.getId())))
                .collect(Collectors.toList());
      } else if (!table.isAvailable()) {
        // Fallback if table is somehow not available but activeOrder is null in current context
        var optOrder = getOrder.execute(table.getId());
        if (optOrder.isPresent()) {
          var order = optOrder.get();
          tableCustomers =
              order.getCustomers().stream()
                  .map(c -> new OrderCustomerModel(c, order.calculateSubtotalByCustomer(c.getId())))
                  .collect(Collectors.toList());
        }
      }

      model.addAttribute("customerMenu", menuModel);
      model.addAttribute("itemTags", ItemTag.values());
      model.addAttribute("showEntryModal", true);
      model.addAttribute("tableCode", tableCode);
      model.addAttribute("tableCustomers", tableCustomers);
      return "customer-menu";
    }

    // Existing joined customer flow
    // Set or refresh cookie for current order
    var orderCookie =
        new jakarta.servlet.http.Cookie("ts_last_order_id", activeOrder.getId().toString());
    orderCookie.setPath("/");
    orderCookie.setMaxAge(60 * 60 * 24); // 24 hours
    response.addCookie(orderCookie);

    var domainLanguages = java.util.List.of(Language.fromLocale(locale));
    var categories = getCategory.execute(restaurant.getId(), domainLanguages);
    var items = getItem.execute(restaurant.getId(), domainLanguages, true);

    CustomerMenuModel menuModel =
        new CustomerMenuModel(
            restaurant,
            categories,
            items,
            table,
            activeOrder,
            dev.thiagooliveira.tablesplit.infrastructure.utils.Time.getZoneId(),
            messageSource);

    // Server-side feedback check
    if (!isNewCustomer) {
      try {
        boolean hasSent = rateItem.hasFeedback(activeOrder.getId(), UUID.fromString(customerId));
        if (hasSent) {
          return String.format("redirect:/@%s/table/%s", slug, tableCode);
        }
      } catch (Exception e) {
        // Ignore check errors
      }
    }

    final var finalActiveOrder = activeOrder;
    List<OrderCustomerModel> tableCustomers =
        activeOrder.getCustomers().stream()
            .map(
                c ->
                    new OrderCustomerModel(
                        c, finalActiveOrder.calculateSubtotalByCustomer(c.getId())))
            .collect(Collectors.toList());

    model.addAttribute("customerMenu", menuModel);
    model.addAttribute("itemTags", ItemTag.values());
    model.addAttribute("showEntryModal", false);
    model.addAttribute("tableCode", tableCode);
    model.addAttribute("tableCustomers", tableCustomers);

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
      var restaurant = getRestaurantBySlug(slug);
      checkPlan(restaurant);
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
    var restaurant = getRestaurantBySlug(slug);
    checkPlan(restaurant);
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
    var restaurant = getRestaurantBySlug(slug);
    checkPlan(restaurant);
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
    var restaurant = getRestaurantBySlug(slug);
    checkPlan(restaurant);

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
    var restaurant = getRestaurantBySlug(slug);
    checkPlan(restaurant);

    transactionalContext.execute(() -> rateItem.execute(request.itemId(), request.rating()));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/@{slug}/table/{tableCode}/feedback/general")
  @ResponseBody
  public ResponseEntity<Void> submitGeneralFeedback(
      @PathVariable String slug,
      @PathVariable String tableCode,
      @RequestBody GeneralFeedbackRequest request) {
    var restaurant = getRestaurantBySlug(slug);
    checkPlan(restaurant);

    transactionalContext.execute(
        () ->
            submitGeneralFeedback.execute(
                request.orderId(), request.customerId(), request.rating(), request.comment()));
    return ResponseEntity.ok().build();
  }

  private void checkPlan(Restaurant restaurant) {
    if (isStarter(restaurant)) {
      throw new NotFoundException("error.plan.feature.not_available");
    }
  }

  private boolean isStarter(Restaurant restaurant) {
    var account =
        getAccount
            .execute(restaurant.getAccountId())
            .orElseThrow(() -> new NotFoundException("error.account.not.found"));
    return account.getEffectivePlan() == Plan.STARTER;
  }

  private String redirectToMenu(Restaurant restaurant) {
    return "redirect:/@" + restaurant.getSlug() + "/menu";
  }

  private Restaurant getRestaurantBySlug(String slug) {
    return getRestaurant
        .execute(slug)
        .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
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

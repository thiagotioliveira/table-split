package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.account.GetAccount;
import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.*;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile.model.ProfileModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
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
  private final GetOrder getOrder;
  private final RateItem rateItem;
  private final MessageSource messageSource;
  private final GetAccount getAccount;

  public CustomerTableController(
      GetRestaurant getRestaurant,
      GetTables getTables,
      GetCategory getCategory,
      GetItem getItem,
      GetOrder getOrder,
      RateItem rateItem,
      MessageSource messageSource,
      GetAccount getAccount) {
    this.getRestaurant = getRestaurant;
    this.getTables = getTables;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.getOrder = getOrder;
    this.rateItem = rateItem;
    this.messageSource = messageSource;
    this.getAccount = getAccount;
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

  @ExceptionHandler(dev.thiagooliveira.tablesplit.domain.order.TableSessionClosedException.class)
  public ResponseEntity<String> handleSessionClosed(
      dev.thiagooliveira.tablesplit.domain.order.TableSessionClosedException ex) {
    return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT)
        .body(ex.getMessage());
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.domain.common.DomainException;
import dev.thiagooliveira.tablesplit.domain.order.IllegalOrderStatusException;
import dev.thiagooliveira.tablesplit.domain.order.OverpaymentException;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ManagerController(Module.TABLES)
@RequestMapping("/tables")
public class TableController {

  private final GetTables getTables;
  private final GetOrder getOrder;
  private final MessageSource messageSource;

  public TableController(GetTables getTables, GetOrder getOrder, MessageSource messageSource) {
    this.getTables = getTables;
    this.getOrder = getOrder;
    this.messageSource = messageSource;
  }

  @GetMapping({"", "/{selectedTableId}"})
  public String index(
      @PathVariable(required = false) UUID selectedTableId, Authentication auth, Model model) {
    this.populateModel(selectedTableId, auth, model);
    return "tables";
  }

  private void populateModel(UUID selectedTableId, Authentication auth, Model model) {
    var context = (AccountContext) auth.getPrincipal();
    model.addAttribute("restaurantSlug", context.getRestaurant().getSlug());
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    model.addAttribute("currencyCode", context.getRestaurant().getCurrency().name());
    model.addAttribute("currency", context.getRestaurant().getCurrency());

    String currencySymbol = context.getRestaurant().getCurrency().getSymbol();
    model.addAttribute("currencySymbol", currencySymbol);

    var userLanguage = context.getUser().getLanguage();
    model.addAttribute("userLanguage", userLanguage);
    model.addAttribute(
        "zoneId", dev.thiagooliveira.tablesplit.infrastructure.timezone.Time.getZoneId().getId());

    // Defaults for when no order is active
    model.addAttribute("orderSubtotal", BigDecimal.ZERO);
    model.addAttribute("orderTotal", BigDecimal.ZERO);
    model.addAttribute("orderPaidAmount", BigDecimal.ZERO);
    model.addAttribute("orderRemainingAmount", BigDecimal.ZERO);
    model.addAttribute("orderServiceFee", context.getRestaurant().getServiceFee());
    model.addAttribute("orderServiceFeeApplied", false);
    model.addAttribute("orderServiceFeeAmount", BigDecimal.ZERO);
    model.addAttribute("orderLoaded", false);

    if (selectedTableId != null) {
      var table =
          getTables
              .findById(selectedTableId)
              .orElseThrow(() -> new NotFoundException("error.table.not.found"));
      model.addAttribute("selectedTableObj", table);
      model.addAttribute("selectedTable", selectedTableId);

      getOrder
          .execute(selectedTableId)
          .ifPresent(
              order -> {
                model.addAttribute("orderLoaded", true);
                model.addAttribute("orderTotal", order.calculateTotal());
                model.addAttribute("orderRemainingAmount", order.calculateRemainingAmount());
              });
    }
    model.addAttribute("orderHistory", java.util.Collections.emptyList());

    model.addAttribute(
        "createTableForm",
        new dev.thiagooliveira.tablesplit.infrastructure.order.web.model.CreateTableForm());
  }

  @ExceptionHandler(TableAlreadyOccupied.class)
  public String handleTableAlreadyOccupied(RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("alert", AlertModel.error("error.table.already.occupied"));
    return "redirect:/tables";
  }

  @ExceptionHandler(TableAlreadyExists.class)
  public String handleTableAlreadyExists(RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("alert", AlertModel.error("error.table.already.exists"));
    return "redirect:/tables";
  }

  @ExceptionHandler(DomainException.class)
  public String handleDomainException(DomainException e, RedirectAttributes redirectAttributes) {
    String messageKey = "error.domain.generic";
    UUID tableId = null;

    if (e instanceof IllegalOrderStatusException ise) {
      tableId = ise.getTableId();
      messageKey =
          switch (ise.getReason()) {
            case PAYMENT_NOT_ALLOWED -> "error.order.payment.not.allowed";
            case PAYMENT_REMOVAL_NOT_ALLOWED -> "error.order.payment.removal.not.allowed";
            case TICKET_NOT_ALLOWED -> "error.order.ticket.not.allowed";
            case CLOSE_NOT_ALLOWED -> "error.order.close.not.allowed";
          };
    } else if (e instanceof OverpaymentException oe) {
      tableId = oe.getTableId();
      messageKey = "error.payment.amount.exceeds.remaining";
    }

    redirectAttributes.addFlashAttribute("alert", AlertModel.error(messageKey));

    if (tableId != null) {
      return "redirect:/tables/" + tableId;
    }
    return "redirect:/tables";
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public String handleIllegalArgumentException(
      IllegalArgumentException e, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("alert", AlertModel.error(e.getMessage()));
    return "redirect:/tables";
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.domain.common.DomainException;
import dev.thiagooliveira.tablesplit.domain.order.IllegalOrderStatusException;
import dev.thiagooliveira.tablesplit.domain.order.OverpaymentException;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.CustomerModel;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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

  @GetMapping({"/content", "/{selectedTableId}/content"})
  public String indexContent(
      @PathVariable(required = false) UUID selectedTableId, Authentication auth, Model model) {
    this.populateModel(selectedTableId, auth, model);
    return "tables :: main-panel";
  }

  private void populateModel(UUID selectedTableId, Authentication auth, Model model) {
    var context = (AccountContext) auth.getPrincipal();
    model.addAttribute("restaurantSlug", context.getRestaurant().getSlug());
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    model.addAttribute("currencyCode", context.getRestaurant().getCurrency().name());
    model.addAttribute("currency", context.getRestaurant().getCurrency());
    model.addAttribute("orderLoaded", false);

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
      var activeOrder = getOrder.execute(selectedTableId);
      if (activeOrder.isPresent()) {
        var order = activeOrder.get();
        model.addAttribute(
            "orderTimeAgo",
            dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.timeAgo(
                order.getOpenedAt(), messageSource, userLanguage));
        model.addAttribute(
            "orderOpenedAtFormatted",
            dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.format(
                order.getOpenedAt()));
        Map<CustomerModel, List<TicketItemModel>> clients = new java.util.LinkedHashMap<>();
        Map<CustomerModel, BigDecimal> clientBalances = new java.util.LinkedHashMap<>();

        List<CustomerModel> customerModels =
            order.getCustomers().stream()
                .map(c -> new CustomerModel(c.getId(), c.getName()))
                .sorted(java.util.Comparator.comparing(CustomerModel::getName))
                .collect(Collectors.toList());

        customerModels.forEach(
            c -> {
              clients.put(c, new java.util.ArrayList<>());
              clientBalances.put(c, BigDecimal.ZERO);
            });

        order.getTickets().stream()
            .flatMap(
                t ->
                    t.getItems().stream()
                        .map(
                            item ->
                                TicketItemModel.fromDomain(
                                    item,
                                    order.getCustomerName(item.getCustomerId()),
                                    t.getNote(),
                                    t.getCreatedAt(),
                                    userLanguage)))
            .forEach(
                item -> {
                  CustomerModel customer =
                      customerModels.stream()
                          .filter(c -> c.getId().equals(item.getCustomerId()))
                          .findFirst()
                          .orElseGet(
                              () ->
                                  new CustomerModel(item.getCustomerId(), item.getCustomerName()));
                  clients.computeIfAbsent(customer, k -> new java.util.ArrayList<>()).add(item);
                  BigDecimal currentBalance =
                      clientBalances.getOrDefault(customer, BigDecimal.ZERO);
                  clientBalances.put(customer, currentBalance.add(item.getTotalPrice()));
                });

        model.addAttribute("clients", clients);
        model.addAttribute("clientBalances", clientBalances);
        model.addAttribute("orderLoaded", true);
        model.addAttribute("orderServiceFee", order.getServiceFee());
        model.addAttribute(
            "orderServiceFeeApplied", order.feeApplied().compareTo(BigDecimal.ZERO) > 0);
        model.addAttribute("orderServiceFeeAmount", order.feeApplied());
        model.addAttribute("orderSubtotal", order.calculateSubtotal());
        model.addAttribute("orderTotal", order.calculateTotal());
        model.addAttribute(
            "payments",
            order.getPayments().stream()
                .map(
                    p ->
                        new OrderHistoryPaymentModel(
                            p.getId().toString(),
                            p.getCustomerId(),
                            p.getAmount(),
                            p.getPaidAt(),
                            p.getMethod().name(),
                            p.getNote()))
                .toList());
        model.addAttribute("orderPaidAmount", order.calculatePaidAmount());
        model.addAttribute("orderRemainingAmount", order.calculateRemainingAmount());
        Map<UUID, String> customerNames = new java.util.HashMap<>();
        customerNames.put(null, "Mesa");
        order.getCustomers().forEach(c -> customerNames.put(c.getId(), c.getName()));
        order
            .getItems()
            .forEach(
                item -> {
                  if (item.getCustomerId() != null
                      && !customerNames.containsKey(item.getCustomerId())) {
                    customerNames.put(item.getCustomerId(), "Cliente");
                  }
                });
        order
            .getPayments()
            .forEach(
                payment -> {
                  if (payment.getCustomerId() != null
                      && !customerNames.containsKey(payment.getCustomerId())) {
                    customerNames.put(payment.getCustomerId(), "Cliente");
                  }
                });
        model.addAttribute("customerNames", customerNames);

        Map<UUID, BigDecimal> clientSubtotals =
            order.getTickets().stream()
                .flatMap(t -> t.getItems().stream())
                .filter(
                    item ->
                        item.getStatus()
                            != dev.thiagooliveira.tablesplit.domain.order.TicketStatus.CANCELLED)
                .collect(
                    Collectors.groupingBy(
                        TicketItem::getCustomerId,
                        Collectors.reducing(
                            BigDecimal.ZERO, TicketItem::getTotalPrice, BigDecimal::add)));

        Map<UUID, BigDecimal> clientPaid =
            order.getPayments().stream()
                .collect(
                    Collectors.groupingBy(
                        dev.thiagooliveira.tablesplit.domain.order.Payment::getCustomerId,
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            dev.thiagooliveira.tablesplit.domain.order.Payment::getAmount,
                            BigDecimal::add)));

        BigDecimal feeFactor =
            BigDecimal.ONE.add(
                BigDecimal.valueOf(order.getServiceFee())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

        // Initial surplus from generic table payments (customerId == null)
        BigDecimal totalSurplus = clientPaid.getOrDefault(null, BigDecimal.ZERO);

        // 1. Calculate raw individual balances and collect surpluses
        Map<CustomerModel, BigDecimal> tempBalances = new java.util.LinkedHashMap<>();
        for (CustomerModel customer : customerModels) {
          BigDecimal subtotal = clientSubtotals.getOrDefault(customer.getId(), BigDecimal.ZERO);
          BigDecimal totalWithFee = subtotal.multiply(feeFactor).setScale(2, RoundingMode.HALF_UP);
          BigDecimal paid = clientPaid.getOrDefault(customer.getId(), BigDecimal.ZERO);
          BigDecimal bal = totalWithFee.subtract(paid);

          if (bal.compareTo(BigDecimal.ZERO) < 0) {
            totalSurplus = totalSurplus.add(bal.abs());
            tempBalances.put(customer, BigDecimal.ZERO);
          } else {
            tempBalances.put(customer, bal);
          }
        }

        // 2. Redistribute totalSurplus to cover remaining debts
        if (totalSurplus.compareTo(BigDecimal.ZERO) > 0) {
          for (CustomerModel customer : customerModels) {
            BigDecimal bal = tempBalances.get(customer);
            if (bal.compareTo(BigDecimal.ZERO) > 0) {
              BigDecimal deduction = bal.min(totalSurplus);
              tempBalances.put(customer, bal.subtract(deduction));
              totalSurplus = totalSurplus.subtract(deduction);
              if (totalSurplus.compareTo(BigDecimal.ZERO) <= 0) break;
            }
          }
        }

        clientBalances.putAll(tempBalances);
        model.addAttribute("clientBalances", clientBalances);
        model.addAttribute("clientSubtotals", clientSubtotals);
      }
      model.addAttribute("orderHistory", java.util.Collections.emptyList());
    }

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

  public record OrderHistoryPaymentModel(
      String id,
      UUID customerId,
      java.math.BigDecimal amount,
      java.time.ZonedDateTime paidAt,
      String method,
      String note) {}
}

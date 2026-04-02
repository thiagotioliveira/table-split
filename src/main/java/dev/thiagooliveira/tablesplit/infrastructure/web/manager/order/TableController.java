package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.CloseTable;
import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.ProcessPayment;
import dev.thiagooliveira.tablesplit.application.order.UpdateTicketItemStatus;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.PaymentMethod;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.CategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.CreateTableForm;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.ItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.OrderHistoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.OrderHistoryPaymentModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TableModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tables")
@ManagerModule(Module.TABLES)
public class TableController {

  private final TransactionalContext transactionalContext;
  private final OpenTable openTable;
  private final CloseTable closeTable;
  private final GetTables getTables;
  private final CreateTable createTable;
  private final GetCategory getCategory;
  private final GetItem getItem;
  private final PlaceOrder placeOrder;
  private final GetOrder getOrder;
  private final ProcessPayment processPayment;
  private final UpdateTicketItemStatus updateTicketItemStatus;

  public TableController(
      TransactionalContext transactionalContext,
      OpenTable openTable,
      CloseTable closeTable,
      GetTables getTables,
      CreateTable createTable,
      GetCategory getCategory,
      GetItem getItem,
      PlaceOrder placeOrder,
      GetOrder getOrder,
      ProcessPayment processPayment,
      UpdateTicketItemStatus updateTicketItemStatus) {
    this.transactionalContext = transactionalContext;
    this.openTable = openTable;
    this.closeTable = closeTable;
    this.getTables = getTables;
    this.createTable = createTable;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.placeOrder = placeOrder;
    this.getOrder = getOrder;
    this.processPayment = processPayment;
    this.updateTicketItemStatus = updateTicketItemStatus;
  }

  @GetMapping
  public String index(
      @RequestParam(required = false) UUID selectedTableId, Authentication auth, Model model) {
    var context = (AccountContext) auth.getPrincipal();

    var result = getTables.execute(context.getRestaurant().getId());
    var tables =
        result.tables().stream()
            .map(
                t -> {
                  var activeOrder = getOrder.execute(t.getId());
                  var balance =
                      activeOrder
                          .map(
                              dev.thiagooliveira.tablesplit.domain.order.Order
                                  ::calculateRemainingAmount)
                          .orElse(BigDecimal.ZERO);
                  return new TableModel(t.getId(), t.getCod(), t.getStatus(), balance);
                })
            .collect(Collectors.toList());

    model.addAttribute("tables", tables);
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("count", result.count());
    model.addAttribute("countAvailable", result.countAvailable());
    model.addAttribute("countOccupied", result.countOccupied());
    model.addAttribute("orderLoaded", false);

    var languages = context.getRestaurant().getCustomerLanguages();
    var categories =
        this.getCategory.execute(context.getRestaurant().getId(), languages).stream()
            .map(c -> new CategoryModel(c.getId(), convertMap(c.getName())))
            .collect(Collectors.toList());
    var menuItems =
        this.getItem.execute(context.getRestaurant().getId(), languages).stream()
            .map(
                i ->
                    new ItemModel(
                        i.getId(), convertMap(i.getName()), i.getPrice(), i.getCategory().getId()))
            .collect(Collectors.toList());
    model.addAttribute("categories", categories);
    model.addAttribute("menuItems", menuItems);

    if (selectedTableId != null) {
      var table = getTables.findById(selectedTableId).orElseThrow();
      model.addAttribute("selectedTableObj", table);
      model.addAttribute("selectedTable", selectedTableId);
      var activeOrder = getOrder.execute(selectedTableId);
      if (activeOrder.isPresent()) {
        var order = activeOrder.get();
        Map<String, List<TicketItemModel>> clients =
            order.getTickets().stream()
                .flatMap(
                    t ->
                        t.getItems().stream()
                            .map(
                                item ->
                                    new TicketItemModel(
                                        item.getId(),
                                        item.getCustomerId(),
                                        order.getCustomerName(item.getCustomerId()),
                                        item.getName().get(Language.PT),
                                        item.getQuantity(),
                                        item.getUnitPrice(),
                                        item.getTotalPrice(),
                                        item.getNote(),
                                        item.getStatus().getLabel(),
                                        item.getStatus().getCssClass(),
                                        t.getCreatedAt())))
                .collect(Collectors.groupingBy(TicketItemModel::getCustomerName));
        model.addAttribute("clients", clients);
        model.addAttribute("orderLoaded", true);
        model.addAttribute("orderServiceFee", order.getServiceFee());
        model.addAttribute("orderServiceFeeApplied", order.feeApplied());
        model.addAttribute("orderSubtotal", order.calculateSubtotal());
        model.addAttribute("orderTotal", order.calculateTotal());
        model.addAttribute("payments", order.getPayments());
        model.addAttribute("orderPaidAmount", order.calculatePaidAmount());
        model.addAttribute("orderPaidAmount", order.calculatePaidAmount());
        model.addAttribute("orderRemainingAmount", order.calculateRemainingAmount());

        Map<String, BigDecimal> clientBalances = new HashMap<>();
        Map<String, BigDecimal> clientSubtotals =
            order.getTickets().stream()
                .flatMap(t -> t.getItems().stream())
                .collect(
                    Collectors.groupingBy(
                        item -> order.getCustomerName(item.getCustomerId()),
                        Collectors.reducing(
                            BigDecimal.ZERO, TicketItem::getTotalPrice, BigDecimal::add)));

        Map<String, BigDecimal> clientPaid =
            order.getPayments().stream()
                .filter(p -> !"unknown".equals(p.getCustomerName()))
                .collect(
                    Collectors.groupingBy(
                        dev.thiagooliveira.tablesplit.domain.order.Payment::getCustomerName,
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            dev.thiagooliveira.tablesplit.domain.order.Payment::getAmount,
                            BigDecimal::add)));

        BigDecimal feeFactor =
            BigDecimal.ONE.add(
                BigDecimal.valueOf(order.getServiceFee())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

        clientSubtotals.forEach(
            (name, subtotal) -> {
              BigDecimal totalWithFee =
                  subtotal.multiply(feeFactor).setScale(2, RoundingMode.HALF_UP);
              BigDecimal paid = clientPaid.getOrDefault(name, BigDecimal.ZERO);
              clientBalances.put(name, totalWithFee.subtract(paid));
            });

        model.addAttribute("clientBalances", clientBalances);
      }
      model.addAttribute(
          "orderHistory",
          getOrder.findAllByTableId(selectedTableId).stream()
              .map(
                  hist ->
                      new OrderHistoryModel(
                          hist.getId().toString(),
                          hist.getTableId().toString(),
                          hist.getServiceFee(),
                          hist.getStatus().name(),
                          hist.getOpenedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                          hist.getClosedAt() != null
                              ? hist.getClosedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                              : null,
                          hist.getTickets().stream()
                              .flatMap(
                                  t ->
                                      t.getItems().stream()
                                          .map(
                                              item ->
                                                  new TicketItemModel(
                                                      item.getId(),
                                                      item.getCustomerId(),
                                                      hist.getCustomerName(item.getCustomerId()),
                                                      item.getName().get(Language.PT),
                                                      item.getQuantity(),
                                                      item.getUnitPrice(),
                                                      item.getTotalPrice(),
                                                      item.getNote(),
                                                      item.getStatus().getLabel(),
                                                      item.getStatus().getCssClass(),
                                                      t.getCreatedAt())))
                              .toList(),
                          hist.getPayments().stream()
                              .map(
                                  pay ->
                                      new OrderHistoryPaymentModel(
                                          pay.getId().toString(),
                                          pay.getCustomerName(),
                                          pay.getAmount(),
                                          pay.getPaidAt()
                                              .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                          pay.getMethod().name(),
                                          pay.getNote()))
                              .toList()))
              .toList());
    }

    model.addAttribute("createTableForm", new CreateTableForm());

    return "tables";
  }

  @PostMapping("/create")
  public String createTable(
      Authentication auth,
      @Valid @ModelAttribute("createTableForm") CreateTableForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      return "tables";
    }

    var context = (AccountContext) auth.getPrincipal();
    transactionalContext.execute(
        () -> createTable.execute(context.getRestaurant().getId(), form.getCod()));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.table.created"));
    return "redirect:/tables";
  }

  @PostMapping("/{tableId}/open")
  public String openTable(
      Authentication auth, @PathVariable UUID tableId, RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    transactionalContext.execute(
        () -> openTable.execute(tableId, context.getRestaurant().getServiceFee(), null, null));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.table.opened"));
    return "redirect:/tables?selectedTableId=" + tableId;
  }

  @PostMapping("/{orderId}/close")
  public String closeTable(@PathVariable UUID orderId, RedirectAttributes redirectAttributes) {

    transactionalContext.execute(() -> closeTable.execute(orderId));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.table.closed"));
    return "redirect:/tables";
  }

  @PostMapping("/{tableId}/order")
  public String placeOrder(
      Authentication auth,
      @PathVariable UUID tableId,
      @ModelAttribute PlaceOrderRequest request,
      RedirectAttributes redirectAttributes) {
    var account = (AccountContext) auth.getPrincipal();
    var table = getTables.findById(tableId).orElseThrow();
    request.setRestaurantId(table.getRestaurantId());
    request.setTableCod(table.getCod());
    request.setServiceFee(account.getRestaurant().getServiceFee());

    transactionalContext.execute(() -> placeOrder.execute(request));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.order.placed"));
    return "redirect:/tables?selectedTableId=" + tableId;
  }

  @PostMapping("/{tableId}/payment")
  public String processPayment(
      @PathVariable UUID tableId,
      @RequestParam String customerName,
      @RequestParam BigDecimal amount,
      @RequestParam(required = false, defaultValue = "CASH") PaymentMethod method,
      @RequestParam(required = false) String note,
      RedirectAttributes redirectAttributes) {

    transactionalContext.execute(
        () -> processPayment.execute(tableId, customerName, amount, method, note));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.payment.processed"));
    return "redirect:/tables?selectedTableId=" + tableId;
  }

  @PostMapping("/{tableId}/items/{itemId}/status")
  public String updateTicketItemStatus(
      @PathVariable UUID tableId,
      @PathVariable UUID itemId,
      @RequestParam TicketStatus status,
      RedirectAttributes redirectAttributes) {

    transactionalContext.execute(() -> updateTicketItemStatus.execute(itemId, status));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.item.status.updated"));
    return "redirect:/tables?selectedTableId=" + tableId;
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

  private static Map<String, String> convertMap(Map<Language, String> map) {
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
  }
}

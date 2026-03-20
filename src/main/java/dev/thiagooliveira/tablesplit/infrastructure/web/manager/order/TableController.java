package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.CloseTable;
import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.TableRepository;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.OrderItem;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.CategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.CreateTableForm;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.ItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.OrderItemModel;
import jakarta.validation.Valid;
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
  private final TableRepository tableRepository;
  private final GetOrder getOrder;

  public TableController(
      TransactionalContext transactionalContext,
      OpenTable openTable,
      CloseTable closeTable,
      GetTables getTables,
      CreateTable createTable,
      GetCategory getCategory,
      GetItem getItem,
      PlaceOrder placeOrder,
      TableRepository tableRepository,
      GetOrder getOrder) {
    this.transactionalContext = transactionalContext;
    this.openTable = openTable;
    this.closeTable = closeTable;
    this.getTables = getTables;
    this.createTable = createTable;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.placeOrder = placeOrder;
    this.tableRepository = tableRepository;
    this.getOrder = getOrder;
  }

  @GetMapping
  public String index(
      @RequestParam(required = false) UUID selectedTableId, Authentication auth, Model model) {
    var context = (AccountContext) auth.getPrincipal();

    var result = getTables.execute(context.getRestaurant().getId());
    model.addAttribute("tables", result.tables());
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
      var table = tableRepository.findById(selectedTableId).orElseThrow();
      model.addAttribute("selectedTableObj", table);
      model.addAttribute("selectedTable", selectedTableId);
      var activeOrder = getOrder.execute(selectedTableId);
      if (activeOrder.isPresent()) {
        var order = activeOrder.get();
        var primaryLanguage = languages.isEmpty() ? Language.PT : languages.get(0);
        var clients =
            order.getItems().stream()
                .collect(
                    Collectors.groupingBy(
                        OrderItem::getCustomerName,
                        Collectors.mapping(
                            item ->
                                new OrderItemModel(
                                    item.getName().getOrDefault(primaryLanguage, "Unknown"),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    item.getTotalPrice(),
                                    item.getNote()),
                            Collectors.toList())));
        model.addAttribute("clients", clients);
        model.addAttribute("orderLoaded", true);
        model.addAttribute("orderTotal", order.calculateTotal());
      }
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
  public String openTable(@PathVariable UUID tableId, RedirectAttributes redirectAttributes) {

    transactionalContext.execute(() -> openTable.execute(tableId));

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
  public String placeOrder(@PathVariable UUID tableId, @ModelAttribute PlaceOrderRequest request) {

    var table = tableRepository.findById(tableId).orElseThrow();
    request.setRestaurantId(table.getRestaurantId());
    request.setTableCod(table.getCod());

    transactionalContext.execute(() -> placeOrder.execute(request));

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

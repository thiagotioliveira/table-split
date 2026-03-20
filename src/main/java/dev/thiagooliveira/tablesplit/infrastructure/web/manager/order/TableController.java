package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.CloseTable;
import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.CreateTableForm;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.OpenTableForm;
import jakarta.validation.Valid;
import java.util.UUID;
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

  public TableController(
      TransactionalContext transactionalContext,
      OpenTable openTable,
      CloseTable closeTable,
      GetTables getTables,
      CreateTable createTable) {
    this.transactionalContext = transactionalContext;
    this.openTable = openTable;
    this.closeTable = closeTable;
    this.getTables = getTables;
    this.createTable = createTable;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    var context = (AccountContext) auth.getPrincipal();

    var result = getTables.execute(context.getRestaurant().getId());
    model.addAttribute("tables", result.tables());
    model.addAttribute("count", result.count());
    model.addAttribute("countAvailable", result.countAvailable());
    model.addAttribute("countOccupied", result.countOccupied());

    model.addAttribute("openTableForm", new OpenTableForm());
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

  @PostMapping("/open")
  public String openTable(
      Authentication auth,
      @Valid @ModelAttribute("openTableForm") OpenTableForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      return "tables";
    }

    var context = (AccountContext) auth.getPrincipal();
    transactionalContext.execute(
        () -> openTable.execute(context.getRestaurant().getId(), form.getTableCod()));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.table.opened"));
    return "redirect:/tables";
  }

  @PostMapping("/{orderId}/close")
  public String closeTable(@PathVariable UUID orderId, RedirectAttributes redirectAttributes) {

    transactionalContext.execute(() -> closeTable.execute(orderId));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.table.closed"));
    return "redirect:/tables";
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
}

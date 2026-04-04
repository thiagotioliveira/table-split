package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.MenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.UpdateCategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.UpdateItemModel;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/menu")
@ManagerModule(Module.MENU)
public class MenuController {

  private static final Logger log = LoggerFactory.getLogger(MenuController.class);

  private final GetCategory getCategory;
  private final CreateCategory createCategory;
  private final UpdateCategory updateCategory;
  private final DeleteCategory deleteCategory;
  private final GetItem getItem;
  private final UpdateItem updateItem;
  private final CreateItem createItem;
  private final DeleteItem deleteItem;
  private final TransactionalContext transactionalContext;

  public MenuController(
      GetCategory getCategory,
      CreateCategory createCategory,
      UpdateCategory updateCategory,
      DeleteCategory deleteCategory,
      GetItem getItem,
      UpdateItem updateItem,
      CreateItem createItem,
      DeleteItem deleteItem,
      TransactionalContext transactionalContext) {
    this.getCategory = getCategory;
    this.createCategory = createCategory;
    this.updateCategory = updateCategory;
    this.deleteCategory = deleteCategory;
    this.getItem = getItem;
    this.updateItem = updateItem;
    this.createItem = createItem;
    this.deleteItem = deleteItem;
    this.transactionalContext = transactionalContext;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    populateModel(auth, model);
    return "menu";
  }

  @PostMapping("/categories")
  public String updateCategory(
      Authentication auth,
      @Valid @ModelAttribute("updateCategoryModel") UpdateCategoryModel updateCategoryModel,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();

    if (bindingResult.hasErrors()) {
      populateModel(auth, model);
      model.addAttribute("openCategoryModal", true);
      return "menu";
    }
    if (updateCategoryModel.getId() == null) {
      this.transactionalContext.execute(
          () ->
              this.createCategory.execute(
                  context.getId(),
                  context.getRestaurant().getId(),
                  updateCategoryModel.toCreateCategoryCommand()));
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.menu.category.created"));
    } else {
      this.transactionalContext.execute(
          () ->
              this.updateCategory.execute(
                  context.getId(),
                  context.getRestaurant().getId(),
                  updateCategoryModel.getId(),
                  updateCategoryModel.toUpdateCategoryCommand()));
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.menu.category.updated"));
    }
    return "redirect:/menu";
  }

  @PostMapping("/items")
  public String updateItem(
      Authentication auth,
      @Valid @ModelAttribute("updateItemModel") UpdateItemModel updateItemModel,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();

    if (bindingResult.hasErrors()) {
      populateModel(auth, model);
      model.addAttribute("openItemModal", true);
      return "menu";
    }
    try {
      if (updateItemModel.getId() == null) {
        this.transactionalContext.execute(
            () ->
                this.createItem.execute(
                    context.getId(),
                    context.getRestaurant().getId(),
                    updateItemModel.toCreateItemCommand()));
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.success("alert.menu.item.created"));
      } else {
        this.transactionalContext.execute(
            () ->
                this.updateItem.execute(
                    context.getId(),
                    context.getRestaurant().getId(),
                    updateItemModel.getId(),
                    updateItemModel.toUpdateItemCommand()));
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.success("alert.menu.item.updated"));
      }
    } catch (dev.thiagooliveira.tablesplit.application.exception.ApplicationException ex) {
      populateModel(auth, model);
      model.addAttribute("alert", AlertModel.error(ex.getMessage()));
      model.addAttribute("openItemModal", true);
      return "menu";
    }

    return "redirect:/menu";
  }

  @PostMapping("/categories/delete")
  public String deleteCategory(
      Authentication auth, @RequestParam UUID categoryId, RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    this.transactionalContext.execute(
        () ->
            this.deleteCategory.execute(
                context.getId(), context.getRestaurant().getId(), categoryId));
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.menu.category.deleted"));
    return "redirect:/menu";
  }

  @PostMapping("/items/delete")
  public String deleteItem(
      Authentication auth, @RequestParam UUID itemId, RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    this.transactionalContext.execute(() -> this.deleteItem.execute(context.getId(), itemId));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.deleted"));
    return "redirect:/menu";
  }

  private void populateModel(Authentication auth, Model model) {
    var context = new ContextModel(auth);
    var languages =
        context.getRestaurant().getCustomerLanguages().stream().map(Language::toDomain).toList();
    var categories = this.getCategory.execute(context.getRestaurant().getId(), languages);
    var items = this.getItem.execute(context.getRestaurant().getId(), languages, true);
    model.addAttribute(
        "menu", new MenuModel(categories, items, context.getRestaurant().getCurrency()));
    model.addAttribute("module", Module.MENU);
    model.addAttribute("context", context);
    if (!model.containsAttribute("updateCategoryModel")) {
      model.addAttribute("updateCategoryModel", new UpdateCategoryModel());
    }
    if (!model.containsAttribute("updateItemModel")) {
      model.addAttribute("updateItemModel", new UpdateItemModel());
    }
    model.addAttribute("languages", context.getRestaurant().getCustomerLanguages());
  }

  @ExceptionHandler(InfrastructureException.class)
  public String handleInfrastructureException(
      InfrastructureException ex, RedirectAttributes redirectAttributes) {
    log.error("An InfrastructureException occurred.", ex);
    redirectAttributes.addFlashAttribute("alert", AlertModel.error(ex.getMessage()));
    return "redirect:/menu";
  }
}

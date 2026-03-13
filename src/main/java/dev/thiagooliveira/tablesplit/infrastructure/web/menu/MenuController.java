package dev.thiagooliveira.tablesplit.infrastructure.web.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.MenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.UpdateCategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.UpdateItemModel;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/menu")
public class MenuController {

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
    var context = new ContextModel(auth);
    var categories = this.getCategory.execute(context.getRestaurant().getId());
    var items = this.getItem.execute(context.getRestaurant().getId());
    model.addAttribute("module", Module.MENU);
    model.addAttribute(
        "menu", new MenuModel(categories, items, context.getRestaurant().getCurrency()));
    model.addAttribute("context", context);
    return "menu";
  }

  @PostMapping("/categories")
  public String updateCategory(
      Authentication auth,
      @ModelAttribute UpdateCategoryModel updateCategoryModel,
      RedirectAttributes redirectAttributes) {
    var context = (UserContext) auth.getPrincipal();
    if (updateCategoryModel.getId() == null) {
      this.transactionalContext.execute(
          () ->
              this.createCategory.execute(
                  context.getAccountId(),
                  context.getRestaurant().getId(),
                  updateCategoryModel.toCreateCategoryCommand()));
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.menu.category.created"));
    } else {
      this.transactionalContext.execute(
          () ->
              this.updateCategory.execute(
                  context.getAccountId(),
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
      @ModelAttribute UpdateItemModel updateItemModel,
      RedirectAttributes redirectAttributes) {
    var context = (UserContext) auth.getPrincipal();
    if (updateItemModel.getId() == null) {
      this.transactionalContext.execute(
          () ->
              this.createItem.execute(
                  context.getAccountId(),
                  context.getRestaurant().getId(),
                  updateItemModel.toCreateItemCommand()));
      redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.created"));
    } else {
      this.transactionalContext.execute(
          () ->
              this.updateItem.execute(
                  context.getAccountId(),
                  context.getRestaurant().getId(),
                  updateItemModel.getId(),
                  updateItemModel.toUpdateItemCommand()));
      redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.updated"));
    }
    return "redirect:/menu";
  }

  @PostMapping("/categories/delete")
  public String deleteCategory(
      Authentication auth, @RequestParam UUID categoryId, RedirectAttributes redirectAttributes) {
    var context = (UserContext) auth.getPrincipal();
    this.transactionalContext.execute(
        () ->
            this.deleteCategory.execute(
                context.getAccountId(), context.getRestaurant().getId(), categoryId));
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.menu.category.deleted"));
    return "redirect:/menu";
  }

  @PostMapping("/items/delete")
  public String deleteItem(
      Authentication auth, @RequestParam UUID itemId, RedirectAttributes redirectAttributes) {
    var context = (UserContext) auth.getPrincipal();
    this.transactionalContext.execute(
        () -> this.deleteItem.execute(context.getAccountId(), itemId));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.deleted"));
    return "redirect:/menu";
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.security.Context;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.MenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.UpdateCategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.UpdateItemModel;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/menu")
public class MenuController {

  private final Context context;
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
      Context context,
      GetCategory getCategory,
      CreateCategory createCategory,
      UpdateCategory updateCategory,
      DeleteCategory deleteCategory,
      GetItem getItem,
      UpdateItem updateItem,
      CreateItem createItem,
      DeleteItem deleteItem,
      TransactionalContext transactionalContext) {
    this.context = context;
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
  public String index(Model model) {
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
      @ModelAttribute UpdateCategoryModel updateCategoryModel,
      RedirectAttributes redirectAttributes) {
    if (updateCategoryModel.getId() == null) {
      this.createCategory.execute(
          context.getRestaurant().getId(), updateCategoryModel.toCreateCategoryCommand());
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.menu.category.created"));
    } else {
      this.updateCategory.execute(
          context.getRestaurant().getId(),
          updateCategoryModel.getId(),
          updateCategoryModel.toUpdateCategoryCommand());
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.menu.category.updated"));
    }
    return "redirect:/menu";
  }

  @PostMapping("/items")
  public String updateItem(
      @ModelAttribute UpdateItemModel updateItemModel, RedirectAttributes redirectAttributes) {
    if (updateItemModel.getId() == null) {
      this.transactionalContext.execute(
          () ->
              this.createItem.execute(
                  context.getRestaurant().getId(), updateItemModel.toCreateItemCommand()));
      redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.created"));
    } else {
      this.transactionalContext.execute(
          () ->
              this.updateItem.execute(
                  context.getRestaurant().getId(),
                  updateItemModel.getId(),
                  updateItemModel.toUpdateItemCommand()));
      redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.updated"));
    }
    return "redirect:/menu";
  }

  @PostMapping("/categories/delete")
  public String deleteCategory(
      @RequestParam UUID categoryId, RedirectAttributes redirectAttributes) {
    this.deleteCategory.execute(context.getRestaurant().getId(), categoryId);
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.menu.category.deleted"));
    return "redirect:/menu";
  }

  @PostMapping("/items/delete")
  public String deleteItem(@RequestParam UUID itemId, RedirectAttributes redirectAttributes) {
    this.deleteItem.execute(itemId);
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.menu.item.deleted"));
    return "redirect:/menu";
  }
}

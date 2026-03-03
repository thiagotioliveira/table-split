package dev.thiagooliveira.tablesplit.infrastructure.web.menu;

import dev.thiagooliveira.tablesplit.application.menu.CreateCategory;
import dev.thiagooliveira.tablesplit.application.menu.DeleteCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.UpdateCategory;
import dev.thiagooliveira.tablesplit.domain.security.Context;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.MenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.menu.model.UpdateCategoryModel;
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

  public MenuController(
      Context context,
      GetCategory getCategory,
      CreateCategory createCategory,
      UpdateCategory updateCategory,
      DeleteCategory deleteCategory) {
    this.context = context;
    this.getCategory = getCategory;
    this.createCategory = createCategory;
    this.updateCategory = updateCategory;
    this.deleteCategory = deleteCategory;
  }

  @GetMapping
  public String index(Model model) {
    var categories = this.getCategory.execute(context.getRestaurant().getId());
    model.addAttribute("module", Module.MENU);
    model.addAttribute("menu", new MenuModel(categories));
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

  @PostMapping("/categories/delete")
  public String deleteCategory(
      @RequestParam UUID categoryId, RedirectAttributes redirectAttributes) {
    this.deleteCategory.execute(context.getRestaurant().getId(), categoryId);
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.menu.category.deleted"));
    return "redirect:/menu";
  }
}

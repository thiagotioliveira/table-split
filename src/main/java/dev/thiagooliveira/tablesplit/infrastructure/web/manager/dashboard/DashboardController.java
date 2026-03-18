package dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard.model.CategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard.model.DashboardModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard.model.ItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard.model.RestaurantModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@ManagerModule(Module.DASHBOARD)
public class DashboardController {

  private final GetRestaurant getRestaurant;
  private final GetCategory getCategory;
  private final GetItem getItem;

  public DashboardController(
      GetRestaurant getRestaurant, GetCategory getCategory, GetItem getItem) {
    this.getRestaurant = getRestaurant;
    this.getCategory = getCategory;
    this.getItem = getItem;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    var context = (AccountContext) auth.getPrincipal();
    var restaurant =
        this.getRestaurant
            .execute(context.getRestaurant().getId())
            .orElseThrow(() -> new InfrastructureException("error.restaurant.not.found"));
    model.addAttribute(
        "dashboard",
        new DashboardModel(
            context,
            new RestaurantModel(restaurant),
            new CategoryModel(
                this.getCategory.count(context.getRestaurant().getId()),
                this.getCategory.countActive(context.getRestaurant().getId()),
                this.getCategory.countInactive(context.getRestaurant().getId())),
            new ItemModel(
                this.getItem
                    .execute(
                        context.getRestaurant().getId(),
                        java.util.List.of(context.getUser().getLanguage()))
                    .stream()
                    .map(ItemModel.Item::new)
                    .toList(),
                this.getItem.count(context.getRestaurant().getId()),
                this.getItem.countActive(context.getRestaurant().getId()),
                this.getItem.countInactive(context.getRestaurant().getId()))));
    return "dashboard";
  }
}

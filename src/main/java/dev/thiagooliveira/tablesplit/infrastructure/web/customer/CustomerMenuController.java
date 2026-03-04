package dev.thiagooliveira.tablesplit.infrastructure.web.customer;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.model.CustomerMenuModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/p")
public class CustomerMenuController {

  private final GetRestaurant getRestaurant;
  private final GetCategory getCategory;
  private final GetItem getItem;

  public CustomerMenuController(
      GetRestaurant getRestaurant, GetCategory getCategory, GetItem getItem) {
    this.getRestaurant = getRestaurant;
    this.getCategory = getCategory;
    this.getItem = getItem;
  }

  @GetMapping("/{slug}/menu")
  public String index(@PathVariable String slug, Model model) {
    var restaurant = getRestaurant.execute(slug).orElseThrow();
    var categories = getCategory.execute(restaurant.getId());
    var items = getItem.execute(restaurant.getId());
    model.addAttribute("customerMenu", new CustomerMenuModel(restaurant, categories, items));
    return "customer-menu";
  }
}

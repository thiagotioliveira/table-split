package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CustomerMenuModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CustomerMenuController {

  private final GetRestaurant getRestaurant;
  private final GetCategory getCategory;
  private final GetItem getItem;

  private final org.springframework.context.MessageSource messageSource;

  public CustomerMenuController(
      GetRestaurant getRestaurant,
      GetCategory getCategory,
      GetItem getItem,
      org.springframework.context.MessageSource messageSource) {
    this.getRestaurant = getRestaurant;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.messageSource = messageSource;
  }

  @GetMapping("/@{slug}/menu")
  public String index(@PathVariable String slug, Model model, Locale locale) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    var requestLanguages = java.util.List.of(Language.fromLocale(locale));
    var categories = getCategory.execute(restaurant.getId(), requestLanguages);
    var allItems = getItem.execute(restaurant.getId(), requestLanguages, true);
    var availableItems =
        allItems.stream()
            .filter(dev.thiagooliveira.tablesplit.domain.menu.Item::isAvailable)
            .toList();
    CustomerMenuModel menuModel =
        new CustomerMenuModel(
            restaurant,
            categories,
            availableItems,
            dev.thiagooliveira.tablesplit.infrastructure.utils.Time.getZoneId(),
            messageSource);
    model.addAttribute("customerMenu", menuModel);
    model.addAttribute(
        "itemTags", dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag.values());
    return "customer-menu";
  }
}

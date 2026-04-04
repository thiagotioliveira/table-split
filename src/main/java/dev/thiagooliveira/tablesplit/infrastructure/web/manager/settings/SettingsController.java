package dev.thiagooliveira.tablesplit.infrastructure.web.manager.settings;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestaurantTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.settings.model.SettingsModel;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@ManagerModule(Module.SETTINGS)
public class SettingsController {

  private final TransactionalContext transactionalContext;
  private final GetRestaurant getRestaurant;
  private final UpdateRestaurant updateRestaurant;

  public SettingsController(
      TransactionalContext transactionalContext,
      GetRestaurant getRestaurant,
      UpdateRestaurant updateRestaurant) {
    this.transactionalContext = transactionalContext;
    this.getRestaurant = getRestaurant;
    this.updateRestaurant = updateRestaurant;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    var context = new ContextModel(auth);
    var restaurant = getRestaurant.execute(context.getRestaurant().getId()).orElseThrow();
    model.addAttribute("module", Module.SETTINGS);
    model.addAttribute("context", context);
    model.addAttribute("form", new SettingsModel(restaurant));
    model.addAttribute("languages", Language.values());
    model.addAttribute("cuisineTypeCodes", CuisineType.values());
    model.addAttribute("restaurantTags", RestaurantTag.values());
    model.addAttribute("averagePriceCodes", AveragePrice.values());
    return "settings";
  }

  @PostMapping
  public String postSettings(
      Authentication auth,
      @Valid @ModelAttribute("form") SettingsModel form,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("languages", Language.values());
      model.addAttribute("cuisineTypeCodes", CuisineType.values());
      model.addAttribute("restaurantTags", RestaurantTag.values());
      model.addAttribute("averagePriceCodes", AveragePrice.values());
      return "settings";
    }
    var context = (AccountContext) auth.getPrincipal();
    var restaurant =
        this.transactionalContext.execute(
            () -> updateRestaurant.execute(context.getRestaurant().getId(), form.toCommand()));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.settings.saved"));
    updateContext(context, restaurant);
    return "redirect:/settings";
  }

  @ExceptionHandler(SlugAlreadyExist.class)
  public String handleSlugAlreadyExist(SlugAlreadyExist ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.error("error.restaurant.slug.already.exist"));
    return "redirect:/register";
  }

  private void updateContext(AccountContext context, Restaurant restaurant) {
    context.getRestaurant().setName(restaurant.getName());
    context.getRestaurant().setCurrency(restaurant.getCurrency());
    context.getRestaurant().setCustomerLanguages(restaurant.getCustomerLanguages());
    context.getRestaurant().setDefaultLanguage(restaurant.getDefaultLanguage());
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.settings;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.security.Context;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.settings.model.SettingsModel;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {

  private final Context context;
  private final GetRestaurant getRestaurant;
  private final UpdateRestaurant updateRestaurant;

  public SettingsController(
      Context context, GetRestaurant getRestaurant, UpdateRestaurant updateRestaurant) {
    this.context = context;
    this.getRestaurant = getRestaurant;
    this.updateRestaurant = updateRestaurant;
  }

  @GetMapping
  public String index(Model model) {
    var restaurant = getRestaurant.execute(context.getRestaurant().getId()).orElseThrow();
    model.addAttribute("module", Module.SETTINGS);
    model.addAttribute("context", context);
    model.addAttribute("form", new SettingsModel(restaurant));
    return "settings";
  }

  @PostMapping
  public String postSettings(
      @Valid @ModelAttribute("form") SettingsModel form,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "settings";
    }
    var restaurant = updateRestaurant.execute(context.getRestaurant().getId(), form.toCommand());
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.settings.saved"));
    updateContext(restaurant);
    return "redirect:/settings";
  }

  private void updateContext(Restaurant restaurant) {
    context.getRestaurant().setName(restaurant.getName());
    context.getRestaurant().setCurrency(restaurant.getCurrency());
    context.getRestaurant().setCustomerLanguages(restaurant.getCustomerLanguages());
  }
}

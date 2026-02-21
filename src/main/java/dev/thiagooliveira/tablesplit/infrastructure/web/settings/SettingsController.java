package dev.thiagooliveira.tablesplit.infrastructure.web.settings;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import dev.thiagooliveira.tablesplit.domain.security.Context;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
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
    var restaurant = getRestaurant.execute(context.getRestaurantId()).orElseThrow();
    var form = new SettingsModel.RestaurantForm(restaurant);
    model.addAttribute("form", form);
    return "settings";
  }

  @PostMapping
  public String postSettings(
      @Valid @ModelAttribute("form") SettingsModel.RestaurantForm form,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "settings";
    }
    updateRestaurant.execute(context.getRestaurantId(), form.toCommand());
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.settings.saved"));
    return "redirect:/settings";
  }
}

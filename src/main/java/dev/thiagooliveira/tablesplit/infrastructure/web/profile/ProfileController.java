package dev.thiagooliveira.tablesplit.infrastructure.web.profile;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.web.profile.model.ProfileModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/p")
public class ProfileController {

  private final GetRestaurant getRestaurant;

  public ProfileController(GetRestaurant getRestaurant) {
    this.getRestaurant = getRestaurant;
  }

  @GetMapping("/{slug}")
  public String index(@PathVariable String slug, Model model) {
    var restaurant = getRestaurant.execute(slug).orElseThrow();
    model.addAttribute("profile", new ProfileModel(restaurant));
    return "restaurant-profile";
  }
}

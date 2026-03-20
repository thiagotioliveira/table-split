package dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile.model.ProfileModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ProfileController {

  private final Time time;
  private final GetRestaurant getRestaurant;

  public ProfileController(Time time, GetRestaurant getRestaurant) {
    this.time = time;
    this.getRestaurant = getRestaurant;
  }

  @GetMapping("/@{slug}")
  public String index(@PathVariable String slug, Model model) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));
    model.addAttribute("profile", new ProfileModel(restaurant, time.getZoneId()));
    return "restaurant-profile";
  }
}

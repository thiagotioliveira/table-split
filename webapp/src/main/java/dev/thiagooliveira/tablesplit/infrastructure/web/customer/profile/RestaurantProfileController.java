package dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile;

import dev.thiagooliveira.tablesplit.application.account.GetAccount;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile.model.ProfileModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class RestaurantProfileController {

  private final Time time;
  private final GetRestaurant getRestaurant;
  private final MessageSource messageSource;
  private final GetAccount getAccount;

  public RestaurantProfileController(
      Time time, GetRestaurant getRestaurant, MessageSource messageSource, GetAccount getAccount) {
    this.time = time;
    this.getRestaurant = getRestaurant;
    this.messageSource = messageSource;
    this.getAccount = getAccount;
  }

  @GetMapping("/@{slug}")
  public String index(@PathVariable String slug, Model model) {
    var restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));

    var account =
        getAccount
            .execute(restaurant.getAccountId())
            .orElseThrow(() -> new NotFoundException("error.restaurant.not.found"));

    if (account.getStatus() != dev.thiagooliveira.tablesplit.domain.account.AccountStatus.ACTIVE
        && account.getStatus()
            != dev.thiagooliveira.tablesplit.domain.account.AccountStatus.TRIAL) {
      throw new NotFoundException("error.restaurant.not.found");
    }

    model.addAttribute("profile", new ProfileModel(restaurant, Time.getZoneId(), messageSource));
    return "restaurant-profile";
  }
}

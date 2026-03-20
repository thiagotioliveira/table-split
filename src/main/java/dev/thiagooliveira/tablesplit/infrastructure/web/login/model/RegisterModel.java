package dev.thiagooliveira.tablesplit.infrastructure.web.login.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import jakarta.validation.Valid;
import java.time.ZoneId;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterModel {
  @Valid private UserModel user = new UserModel();
  @Valid private RestaurantModel restaurant = new RestaurantModel();

  public CreateAccountCommand toCommand(PasswordEncoder passwordEncoder, ZoneId zone) {
    return new CreateAccountCommand(
        this.user.toCommand(passwordEncoder), this.getRestaurant().toCommand(), zone);
  }

  public UserModel getUser() {
    return user;
  }

  public void setUser(UserModel user) {
    this.user = user;
  }

  public RestaurantModel getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(RestaurantModel restaurant) {
    this.restaurant = restaurant;
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.login.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterModel {
  @Valid private UserModel user = new UserModel();
  @Valid private RestaurantModel restaurant = new RestaurantModel();
  @NotNull private Plan plan = Plan.STARTER;

  public CreateAccountCommand toCommand(PasswordEncoder passwordEncoder, ZoneId zone) {
    return new CreateAccountCommand(
        this.user.toCommand(passwordEncoder), this.getRestaurant().toCommand(), zone, plan);
  }

  public Plan getPlan() {
    return plan;
  }

  public void setPlan(Plan plan) {
    this.plan = plan;
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

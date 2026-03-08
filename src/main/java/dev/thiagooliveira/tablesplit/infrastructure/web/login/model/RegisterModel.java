package dev.thiagooliveira.tablesplit.infrastructure.web.login.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;

public class RegisterModel {
  private UserModel user = new UserModel();
  private RestaurantModel restaurant = new RestaurantModel();

  public CreateAccountCommand toCommand() {
    return new CreateAccountCommand(this.user.toCommand(), this.getRestaurant().toCommand());
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

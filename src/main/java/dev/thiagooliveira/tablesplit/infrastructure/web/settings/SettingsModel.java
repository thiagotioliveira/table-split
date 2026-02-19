package dev.thiagooliveira.tablesplit.infrastructure.web.settings;

import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import jakarta.validation.constraints.NotBlank;

public class SettingsModel {

  public static class RestaurantForm {
    @NotBlank private String name;

    public RestaurantForm() {}

    public RestaurantForm(Restaurant restaurant) {
      this.name = restaurant.getName();
    }

    public UpdateRestaurantCommand toCommand() {
      return new UpdateRestaurantCommand(this.name);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}

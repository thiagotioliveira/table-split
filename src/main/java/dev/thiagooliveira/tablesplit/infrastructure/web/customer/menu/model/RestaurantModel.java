package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;

public class RestaurantModel {
  private final String name;
  private final String address;
  private final Currency currency;
  private final String time;

  public RestaurantModel(Restaurant restaurant) {
    this.name = restaurant.getName();
    this.address = restaurant.getAddress();
    this.currency = restaurant.getCurrency();
    this.time = "18:00 - 00:00";
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public String getTime() {
    return time;
  }

  public Currency getCurrency() {
    return currency;
  }
}

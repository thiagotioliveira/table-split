package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.infrastructure.security.context.RestaurantContext;
import java.util.UUID;

public class RestaurantModel {
  private final UUID id;
  private String name;
  private String address;
  private String slug;
  private String currencySymbol;

  public RestaurantModel(RestaurantContext restaurant) {
    this.id = restaurant.getId();
    this.name = restaurant.getName();
    this.address = restaurant.getAddress();
    this.slug = restaurant.getSlug();
    this.currencySymbol = restaurant.getCurrency().getSymbol();
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }
}

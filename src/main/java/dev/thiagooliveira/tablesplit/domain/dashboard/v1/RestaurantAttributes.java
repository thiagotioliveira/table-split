package dev.thiagooliveira.tablesplit.domain.dashboard.v1;

import java.util.UUID;

public class RestaurantAttributes {
  private final UUID id;
  private String name;
  private String address;
  private String slug;
  private String currencySymbol;

  public RestaurantAttributes(
      UUID id, String name, String address, String slug, String currencySymbol) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.slug = slug;
    this.currencySymbol = currencySymbol;
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

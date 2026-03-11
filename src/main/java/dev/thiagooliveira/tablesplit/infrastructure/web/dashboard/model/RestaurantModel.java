package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.domain.dashboard.v1.RestaurantAttributes;
import java.util.UUID;

public class RestaurantModel {
  private final UUID id;
  private String name;
  private String address;
  private String slug;
  private String currencySymbol;

  public RestaurantModel(RestaurantAttributes attributes) {
    this.id = attributes.getId();
    this.name = attributes.getName();
    this.address = attributes.getAddress();
    this.slug = attributes.getSlug();
    this.currencySymbol = attributes.getCurrencySymbol();
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
}

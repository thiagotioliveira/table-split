package dev.thiagooliveira.tablesplit.infrastructure.web.login.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;

public class RestaurantModel {
  private String name;
  private String slug;
  private String description;
  private String phone;
  private String email;
  private String website;
  private String address;
  private String currency;
  private int serviceFee = 10;

  public CreateRestaurantCommand toCommand() {
    return new CreateRestaurantCommand(
        this.name,
        this.slug,
        this.description,
        this.phone,
        this.email,
        this.website,
        this.address,
        this.currency,
        this.serviceFee);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(int serviceFee) {
    this.serviceFee = serviceFee;
  }
}

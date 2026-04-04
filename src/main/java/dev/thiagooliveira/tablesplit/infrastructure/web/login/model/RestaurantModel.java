package dev.thiagooliveira.tablesplit.infrastructure.web.login.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestaurantTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RestaurantModel {
  @NotBlank private String name;
  @NotBlank private String slug;
  private String description;
  @NotBlank private String phone;
  @NotBlank private String email;
  private String website;
  @NotBlank private String address;
  @NotNull private Currency currency;
  @NotNull private Integer serviceFee = 10;
  private CuisineType cuisineType;
  private java.util.List<RestaurantTag> tags = new java.util.ArrayList<>();

  @Min(0)
  @NotNull
  private Integer numberOfTables = 0;

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
        this.serviceFee,
        this.numberOfTables,
        this.cuisineType != null
            ? dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType.valueOf(
                this.cuisineType.name())
            : null,
        this.tags != null
            ? this.tags.stream().map(t -> Tag.valueOf(t.name())).toList()
            : java.util.List.of());
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

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public Integer getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(Integer serviceFee) {
    this.serviceFee = serviceFee;
  }

  public Integer getNumberOfTables() {
    return numberOfTables;
  }

  public void setNumberOfTables(Integer numberOfTables) {
    this.numberOfTables = numberOfTables;
  }

  public CuisineType getCuisineType() {
    return cuisineType;
  }

  public void setCuisineType(CuisineType cuisineType) {
    this.cuisineType = cuisineType;
  }

  public java.util.List<RestaurantTag> getTags() {
    return tags;
  }

  public void setTags(java.util.List<RestaurantTag> tags) {
    this.tags = tags;
  }
}

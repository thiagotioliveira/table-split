package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.List;
import java.util.UUID;

public class RestaurantContext {
  private final UUID id;
  private String name;
  private String slug;
  private Currency currency;
  private int serviceFee;
  private List<Language> customerLanguages;

  public RestaurantContext(Restaurant restaurant) {
    this.id = restaurant.getId();
    this.name = restaurant.getName();
    this.slug = restaurant.getSlug();
    this.currency = restaurant.getCurrency();
    this.serviceFee = restaurant.getServiceFee();
    this.customerLanguages = restaurant.getCustomerLanguages();
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

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public List<Language> getCustomerLanguages() {
    return customerLanguages;
  }

  public void setCustomerLanguages(List<Language> customerLanguages) {
    this.customerLanguages = customerLanguages;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public int getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(int serviceFee) {
    this.serviceFee = serviceFee;
  }
}

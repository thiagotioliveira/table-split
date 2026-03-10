package dev.thiagooliveira.tablesplit.domain.security;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.List;
import java.util.UUID;

public class RestaurantContext {

  private final UUID id;
  private String name;
  private String currency;
  private List<Language> customerLanguages;

  public RestaurantContext(
      UUID id, String name, String currency, List<Language> customerLanguages) {
    this.id = id;
    this.name = name;
    this.currency = currency;
    this.customerLanguages = customerLanguages;
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

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public List<Language> getCustomerLanguages() {
    return customerLanguages;
  }

  public void setCustomerLanguages(List<Language> customerLanguages) {
    this.customerLanguages = customerLanguages;
  }
}

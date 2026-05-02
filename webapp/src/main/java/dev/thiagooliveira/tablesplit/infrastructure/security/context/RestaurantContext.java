package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.ThemeConfig;
import java.util.List;
import java.util.UUID;

public class RestaurantContext {
  private final UUID id;
  private String name;
  private String slug;
  private Currency currency;
  private int serviceFee;
  private List<Language> customerLanguages;
  private Language defaultLanguage;
  private ThemeContext theme;

  public RestaurantContext(Restaurant restaurant) {
    this.id = restaurant.getId();
    this.name = restaurant.getName();
    this.slug = restaurant.getSlug();
    this.currency = restaurant.getCurrency();
    this.serviceFee = restaurant.getServiceFee();
    this.customerLanguages = restaurant.getCustomerLanguages();
    this.defaultLanguage = restaurant.getDefaultLanguage();
    this.theme = ThemeContext.from(ThemeConfig.resolve(restaurant));
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

  public Language getDefaultLanguage() {
    return defaultLanguage;
  }

  public void setDefaultLanguage(Language defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
  }

  public ThemeContext getTheme() {
    return theme;
  }

  public void setTheme(ThemeContext theme) {
    this.theme = theme;
  }
}

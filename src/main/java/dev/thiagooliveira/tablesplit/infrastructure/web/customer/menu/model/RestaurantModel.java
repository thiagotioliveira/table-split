package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery.model.RestaurantImageModel;
import java.util.List;

public class RestaurantModel {
  private final String name;
  private final String address;
  private final String slug;
  private final Currency currency;
  private final String cuisineType;
  private final String time;
  private final List<RestaurantImageModel> images;
  private final List<Language> customerLanguages;
  private final Language defaultLanguage;

  public RestaurantModel(Restaurant restaurant) {
    this.name = restaurant.getName();
    this.address = restaurant.getAddress();
    this.slug = restaurant.getSlug();
    this.currency = restaurant.getCurrency();
    this.cuisineType =
        restaurant.getCuisineType() != null
            ? dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType
                .valueOf(restaurant.getCuisineType().name())
                .getLabel()
            : null;
    this.time = "18:00 - 00:00";
    this.images =
        restaurant.getImages() == null
            ? List.of()
            : restaurant.getImages().stream().map(RestaurantImageModel::new).toList();
    this.customerLanguages =
        restaurant.getCustomerLanguages().stream().map(Language::fromDomain).toList();
    this.defaultLanguage = Language.fromDomain(restaurant.getDefaultLanguage());
  }

  public String getSlug() {
    return slug;
  }

  public String getCuisineType() {
    return cuisineType;
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

  public List<RestaurantImageModel> getImages() {
    return images;
  }

  public List<RestaurantImageModel> getCoverImages() {
    return images.stream().filter(RestaurantImageModel::isCover).toList();
  }

  public Currency getCurrency() {
    return currency;
  }

  public List<dev.thiagooliveira.tablesplit.infrastructure.web.Language> getCustomerLanguages() {
    return customerLanguages;
  }

  public dev.thiagooliveira.tablesplit.infrastructure.web.Language getDefaultLanguage() {
    return defaultLanguage;
  }
}

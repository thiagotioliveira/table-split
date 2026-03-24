package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.List;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestauranteImageModel;

public class RestaurantModel {
  private final String name;
  private final String address;
  private final Currency currency;
  private final String cuisineType;
  private final String time;
  private final List<RestauranteImageModel> images;

  public RestaurantModel(Restaurant restaurant) {
    this.name = restaurant.getName();
    this.address = restaurant.getAddress();
    this.currency = restaurant.getCurrency();
    this.cuisineType =
        restaurant.getCuisineType() != null
            ? CuisineType.valueOf(restaurant.getCuisineType().name()).getLabel()
            : null;
    this.time = "18:00 - 00:00";
    this.images =
        restaurant.getImages() == null
            ? List.of()
            : restaurant.getImages().stream().map(RestauranteImageModel::new).toList();
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

  public List<RestauranteImageModel> getImages() {
    return images;
  }
 
  public List<RestauranteImageModel> getCoverImages() {
    return images.stream().filter(RestauranteImageModel::isCover).toList();
  }

  public Currency getCurrency() {
    return currency;
  }
}

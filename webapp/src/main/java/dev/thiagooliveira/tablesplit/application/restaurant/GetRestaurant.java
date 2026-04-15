package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.Optional;
import java.util.UUID;

public class GetRestaurant {

  private final RestaurantRepository restaurantRepository;

  public GetRestaurant(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public Optional<Restaurant> execute(UUID restaurantId) {
    return this.restaurantRepository.findById(restaurantId);
  }

  public Optional<Restaurant> execute(String slug) {
    return this.restaurantRepository.findBySlug(slug);
  }
}

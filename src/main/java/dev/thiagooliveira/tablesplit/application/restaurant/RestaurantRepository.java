package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

  Optional<Restaurant> findById(UUID restaurantId);

  Optional<Restaurant> findBySlug(String slug);

  void save(Restaurant restaurant);
}

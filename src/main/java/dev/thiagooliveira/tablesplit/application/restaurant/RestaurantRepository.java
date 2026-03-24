package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

  Optional<Restaurant> findById(UUID restaurantId);

  Optional<Restaurant> findByAccountId(UUID accountId);

  Optional<Restaurant> findBySlug(String slug);

  void save(Restaurant restaurant);

  List<RestaurantImage> findImagesByRestaurantId(UUID restaurantId);

  void saveImage(RestaurantImage image);

  void deleteImage(UUID imageId);

  Optional<RestaurantImage> findImageById(UUID imageId);
}

package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestauranteImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

  Optional<Restaurant> findById(UUID restaurantId);

  Optional<Restaurant> findByAccountId(UUID accountId);

  Optional<Restaurant> findBySlug(String slug);

  void save(Restaurant restaurant);

  List<RestauranteImage> findImagesByRestaurantId(UUID restaurantId);

  void saveImage(RestauranteImage image);

  void deleteImage(UUID imageId);

  Optional<RestauranteImage> findImageById(UUID imageId);
}

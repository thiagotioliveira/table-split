package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageStorage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.UUID;

public class DeleteRestaurantImage {
  private final RestaurantRepository restaurantRepository;
  private final RestaurantImageStorage restaurantImageStorage;

  public DeleteRestaurantImage(
      RestaurantRepository restaurantRepository, RestaurantImageStorage restaurantImageStorage) {
    this.restaurantRepository = restaurantRepository;
    this.restaurantImageStorage = restaurantImageStorage;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID imageId) {
    restaurantRepository.deleteImage(imageId);
    restaurantImageStorage.delete(accountId, restaurantId, imageId);
  }
}

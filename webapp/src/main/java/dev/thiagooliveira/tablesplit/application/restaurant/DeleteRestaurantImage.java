package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.UUID;

public class DeleteRestaurantImage {
  private final RestaurantRepository restaurantRepository;
  private final ImageStorage imageStorage;

  public DeleteRestaurantImage(
      RestaurantRepository restaurantRepository, ImageStorage imageStorage) {
    this.restaurantRepository = restaurantRepository;
    this.imageStorage = imageStorage;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID imageId) {
    restaurantRepository.deleteImage(imageId);
    imageStorage.deleteRestaurantGallery(accountId, restaurantId, imageId);
  }
}

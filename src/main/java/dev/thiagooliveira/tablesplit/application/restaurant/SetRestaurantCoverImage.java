package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import java.util.List;
import java.util.UUID;

public class SetRestaurantCoverImage {
  private final RestaurantRepository restaurantRepository;

  public SetRestaurantCoverImage(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public void execute(UUID restaurantId, UUID imageId) {
    List<RestaurantImage> images = restaurantRepository.findImagesByRestaurantId(restaurantId);
    for (RestaurantImage img : images) {
      img.setCover(img.getId().equals(imageId));
      restaurantRepository.saveImage(img);
    }
  }
}

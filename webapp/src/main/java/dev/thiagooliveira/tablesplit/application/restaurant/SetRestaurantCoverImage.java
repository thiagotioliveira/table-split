package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.List;
import java.util.UUID;

public class SetRestaurantCoverImage {
  private final RestaurantRepository restaurantRepository;

  public SetRestaurantCoverImage(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public void execute(UUID restaurantId, UUID imageId, boolean isCover) {
    List<RestaurantImage> images = restaurantRepository.findImagesByRestaurantId(restaurantId);
    for (RestaurantImage img : images) {
      if (img.getId().equals(imageId)) {
        img.setCover(isCover);
        restaurantRepository.saveImage(img);
        break;
      }
    }
  }
}

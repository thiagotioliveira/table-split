package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestauranteImage;
import java.util.List;
import java.util.UUID;

public class SetRestaurantCoverImage {
  private final RestaurantRepository restaurantRepository;

  public SetRestaurantCoverImage(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public void execute(UUID restaurantId, UUID imageId) {
    List<RestauranteImage> images = restaurantRepository.findImagesByRestaurantId(restaurantId);
    for (RestauranteImage img : images) {
      img.setCover(img.getId().equals(imageId));
      restaurantRepository.saveImage(img);
    }
  }
}

package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestauranteImage;
import java.util.List;
import java.util.UUID;

public class GetRestaurantImages {
  private final RestaurantRepository restaurantRepository;

  public GetRestaurantImages(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public List<RestauranteImage> execute(UUID restaurantId) {
    return restaurantRepository.findImagesByRestaurantId(restaurantId);
  }
}

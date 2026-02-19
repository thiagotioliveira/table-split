package dev.thiagooliveira.tablesplit.application.restaurant;

import java.util.UUID;

public class UpdateRestaurant {

  private final GetRestaurant getRestaurant;
  private final RestaurantRepository restaurantRepository;

  public UpdateRestaurant(GetRestaurant getRestaurant, RestaurantRepository restaurantRepository) {
    this.getRestaurant = getRestaurant;
    this.restaurantRepository = restaurantRepository;
  }

  public void execute(UUID restaurantId, UpdateRestaurantCommand command) {
    var restaurant = getRestaurant.execute(restaurantId).orElseThrow();
    restaurant.setName(command.name());
    restaurantRepository.save(restaurant);
  }
}

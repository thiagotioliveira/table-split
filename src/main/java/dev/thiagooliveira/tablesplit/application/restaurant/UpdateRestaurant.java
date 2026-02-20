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
    restaurant.setDescription(command.description());
    restaurant.setPhone(command.phone());
    restaurant.setEmail(command.email());
    restaurant.setAddress(command.address());
    restaurant.setDefaultLanguage(command.defaultLanguage());
    restaurant.setCurrency(command.currency());
    restaurant.setServiceFee(command.serviceFee());
    restaurant.setAveragePrice(command.averagePrice());
    restaurant.setHashPrimaryColor(command.hashPrimaryColor());
    restaurant.setHashAccentColor(command.hashAccentColor());
    restaurantRepository.save(restaurant);
  }
}

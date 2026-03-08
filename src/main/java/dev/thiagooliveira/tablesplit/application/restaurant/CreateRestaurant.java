package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class CreateRestaurant {

  private final GetRestaurant getRestaurant;
  private final RestaurantRepository restaurantRepository;

  public CreateRestaurant(GetRestaurant getRestaurant, RestaurantRepository restaurantRepository) {
    this.getRestaurant = getRestaurant;
    this.restaurantRepository = restaurantRepository;
  }

  public Restaurant execute(UUID accountId, CreateRestaurantCommand command) {
    if (getRestaurant.execute(command.slug()).isPresent()) {
      throw new RuntimeException(); // TODO
    }
    var restaurant = new Restaurant();
    restaurant.setId(UUID.randomUUID());
    restaurant.setAccountId(accountId);
    restaurant.setName(command.name());
    restaurant.setSlug(command.slug().toLowerCase().trim());
    restaurant.setDescription(command.description());
    restaurant.setWebsite(command.website());
    restaurant.setPhone(command.phone());
    restaurant.setEmail(command.email());
    restaurant.setAddress(command.address());
    restaurant.setCuisineType(command.cuisineType());
    restaurant.setDefaultLanguage(command.defaultLanguage());
    restaurant.setCustomerLanguages(command.customerLanguages());
    restaurant.setTags(command.tags());
    restaurant.setCurrency(command.currency());
    restaurant.setServiceFee(command.serviceFee());
    restaurant.setAveragePrice(command.averagePrice());
    restaurant.setDays(command.days());
    restaurant.setHashPrimaryColor(command.hashPrimaryColor());
    restaurant.setHashAccentColor(command.hashAccentColor());
    restaurantRepository.save(restaurant);
    return restaurant;
  }
}

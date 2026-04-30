package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class CreateRestaurant {

  private final RestaurantRepository restaurantRepository;

  public CreateRestaurant(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public Restaurant execute(UUID accountId, CreateRestaurantCommand command) {
    if (this.restaurantRepository.findBySlug(command.slug()).isPresent()) {
      throw new SlugAlreadyExist();
    }
    var restaurant =
        Restaurant.create(
            UUID.randomUUID(),
            accountId,
            command.name(),
            command.slug().toLowerCase().trim(),
            command.numberOfTables());

    restaurant.setDescription(command.description());
    restaurant.setWebsite(command.website());
    restaurant.setPhone(command.phone());
    restaurant.setEmail(command.email());
    restaurant.setAddress(command.address());
    restaurant.setCuisineType(command.cuisineType());
    restaurant.setCustomerLanguages(command.customerLanguages());
    restaurant.setTags(command.tags());
    restaurant.setCurrency(command.currency());
    restaurant.setServiceFee(command.serviceFee());
    restaurant.setAveragePrice(command.averagePrice());
    restaurant.setDays(command.days());
    restaurant.setHashPrimaryColor(command.hashPrimaryColor());
    restaurant.setHashAccentColor(command.hashAccentColor());
    restaurant.setDefaultLanguage(command.defaultLanguage());

    restaurantRepository.save(restaurant);
    return restaurant;
  }
}

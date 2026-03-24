package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.restaurant.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class CreateRestaurant {

  private final EventPublisher eventPublisher;
  private final RestaurantRepository restaurantRepository;

  public CreateRestaurant(
      EventPublisher eventPublisher, RestaurantRepository restaurantRepository) {
    this.eventPublisher = eventPublisher;
    this.restaurantRepository = restaurantRepository;
  }

  public Restaurant execute(UUID accountId, CreateRestaurantCommand command) {
    if (this.restaurantRepository.findBySlug(command.slug()).isPresent()) {
      throw new SlugAlreadyExist();
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
    restaurant.setCustomerLanguages(command.customerLanguages());
    restaurant.setTags(command.tags());
    restaurant.setCurrency(command.currency());
    restaurant.setServiceFee(command.serviceFee());
    restaurant.setAveragePrice(command.averagePrice());
    restaurant.setDays(command.days());
    restaurant.setHashPrimaryColor(command.hashPrimaryColor());
    restaurant.setHashAccentColor(command.hashAccentColor());
    restaurantRepository.save(restaurant);
    this.eventPublisher.publishEvent(
        new RestaurantCreatedEvent(restaurant, command.numberOfTables()));
    return restaurant;
  }
}

package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.command.UpdateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class UpdateRestaurant {

  private final RestaurantRepository restaurantRepository;

  public UpdateRestaurant(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public Restaurant execute(UUID restaurantId, UpdateRestaurantCommand command) {
    var restaurant = this.restaurantRepository.findById(restaurantId).orElseThrow();
    if (!restaurant.getSlug().equals(command.slug())) {
      this.restaurantRepository
          .findBySlug(command.slug())
          .ifPresent(
              r -> {
                throw new SlugAlreadyExist();
              });
    }
    restaurant.update(
        command.name(),
        command.slug().toLowerCase().trim(),
        command.description(),
        command.website(),
        command.phone(),
        command.email(),
        command.address(),
        command.cuisineType(),
        command.customerLanguages(),
        command.tags(),
        command.currency(),
        command.serviceFee(),
        command.averagePrice(),
        command.days(),
        command.hashPrimaryColor(),
        command.hashAccentColor());

    restaurantRepository.save(restaurant);
    return restaurant;
  }
}

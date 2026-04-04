package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.restaurant.command.UpdateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.List;
import java.util.UUID;

public class UpdateRestaurant {

  private final EventPublisher eventPublisher;
  private final RestaurantRepository restaurantRepository;

  public UpdateRestaurant(
      EventPublisher eventPublisher, RestaurantRepository restaurantRepository) {
    this.eventPublisher = eventPublisher;
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
    var oldLanguages =
        restaurant.getCustomerLanguages() != null
            ? restaurant.getCustomerLanguages()
            : List.<Language>of();
    var newLanguages =
        command.customerLanguages() != null ? command.customerLanguages() : List.<Language>of();

    List<Language> added = newLanguages.stream().filter(l -> !oldLanguages.contains(l)).toList();
    List<Language> removed = oldLanguages.stream().filter(l -> !newLanguages.contains(l)).toList();

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
    this.eventPublisher.publishEvent(new RestaurantUpdatedEvent(restaurant, added, removed));
    return restaurant;
  }
}

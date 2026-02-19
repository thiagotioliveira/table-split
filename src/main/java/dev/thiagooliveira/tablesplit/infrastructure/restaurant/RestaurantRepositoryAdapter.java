package dev.thiagooliveira.tablesplit.infrastructure.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RestaurantRepositoryAdapter implements RestaurantRepository {

  private final RestaurantJpaRepository restaurantJpaRepository;

  public RestaurantRepositoryAdapter(RestaurantJpaRepository restaurantJpaRepository) {
    this.restaurantJpaRepository = restaurantJpaRepository;
  }

  @Override
  public Optional<Restaurant> findById(UUID restaurantId) {
    return this.restaurantJpaRepository.findById(restaurantId).map(RestaurantEntity::toDomain);
  }

  @Override
  public void save(Restaurant restaurant) {
    this.restaurantJpaRepository.save(RestaurantEntity.fromDomain(restaurant));
  }
}

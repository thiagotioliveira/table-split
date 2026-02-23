package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
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
  public Optional<Restaurant> findBySlug(String slug) {
    return this.restaurantJpaRepository.findBySlug(slug).map(RestaurantEntity::toDomain);
  }

  @Override
  public void save(Restaurant restaurant) {
    this.restaurantJpaRepository.save(RestaurantEntity.fromDomain(restaurant));
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestauranteImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RestaurantRepositoryAdapter implements RestaurantRepository {

  private final RestaurantJpaRepository restaurantJpaRepository;
  private final RestauranteImageJpaRepository restauranteImageJpaRepository;

  public RestaurantRepositoryAdapter(
      RestaurantJpaRepository restaurantJpaRepository,
      RestauranteImageJpaRepository restauranteImageJpaRepository) {
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.restauranteImageJpaRepository = restauranteImageJpaRepository;
  }

  @Override
  public Optional<Restaurant> findById(UUID restaurantId) {
    return this.restaurantJpaRepository.findById(restaurantId)
        .map(RestaurantEntity::toDomain)
        .map(r -> {
          r.setImages(findImagesByRestaurantId(r.getId()));
          return r;
        });
  }

  @Override
  public Optional<Restaurant> findByAccountId(UUID accountId) {
    return this.restaurantJpaRepository.findByAccountId(accountId)
        .map(RestaurantEntity::toDomain)
        .map(r -> {
          r.setImages(findImagesByRestaurantId(r.getId()));
          return r;
        });
  }

  @Override
  public Optional<Restaurant> findBySlug(String slug) {
    return this.restaurantJpaRepository.findBySlug(slug)
        .map(RestaurantEntity::toDomain)
        .map(r -> {
          r.setImages(findImagesByRestaurantId(r.getId()));
          return r;
        });
  }

  @Override
  public void save(Restaurant restaurant) {
    this.restaurantJpaRepository.save(RestaurantEntity.fromDomain(restaurant));
  }

  @Override
  public List<RestauranteImage> findImagesByRestaurantId(UUID restaurantId) {
    return this.restauranteImageJpaRepository.findByRestaurantId(restaurantId).stream()
        .map(RestauranteImageEntity::toDomain)
        .toList();
  }

  @Override
  public void saveImage(RestauranteImage image) {
    this.restauranteImageJpaRepository.save(RestauranteImageEntity.fromDomain(image));
  }

  @Override
  public void deleteImage(UUID imageId) {
    this.restauranteImageJpaRepository.deleteById(imageId);
  }

  @Override
  public Optional<RestauranteImage> findImageById(UUID imageId) {
    return this.restauranteImageJpaRepository.findById(imageId).map(RestauranteImageEntity::toDomain);
  }
}

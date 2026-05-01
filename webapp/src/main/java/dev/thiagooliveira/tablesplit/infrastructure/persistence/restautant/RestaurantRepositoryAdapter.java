package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RestaurantRepositoryAdapter implements RestaurantRepository {

  private final RestaurantJpaRepository restaurantJpaRepository;
  private final RestauranteImageJpaRepository restauranteImageJpaRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;
  private final RestaurantEntityMapper restaurantMapper;
  private final RestaurantImageEntityMapper imageMapper;

  public RestaurantRepositoryAdapter(
      RestaurantJpaRepository restaurantJpaRepository,
      RestauranteImageJpaRepository restauranteImageJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher,
      RestaurantEntityMapper restaurantMapper,
      RestaurantImageEntityMapper imageMapper) {
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.restauranteImageJpaRepository = restauranteImageJpaRepository;
    this.eventPublisher = eventPublisher;
    this.restaurantMapper = restaurantMapper;
    this.imageMapper = imageMapper;
  }

  @Override
  public Optional<Restaurant> findById(UUID restaurantId) {
    return this.restaurantJpaRepository
        .findById(restaurantId)
        .map(restaurantMapper::toDomain)
        .map(
            r -> {
              r.setImages(findImagesByRestaurantId(r.getId()));
              return r;
            });
  }

  @Override
  public Optional<Restaurant> findByAccountId(UUID accountId) {
    return this.restaurantJpaRepository
        .findByAccountId(accountId)
        .map(restaurantMapper::toDomain)
        .map(
            r -> {
              r.setImages(findImagesByRestaurantId(r.getId()));
              return r;
            });
  }

  @Override
  public Optional<Restaurant> findBySlug(String slug) {
    return this.restaurantJpaRepository
        .findBySlug(slug)
        .map(restaurantMapper::toDomain)
        .map(
            r -> {
              r.setImages(findImagesByRestaurantId(r.getId()));
              return r;
            });
  }

  @Override
  public void save(Restaurant restaurant) {
    this.restaurantJpaRepository.save(restaurantMapper.toEntity(restaurant));
    restaurant.getDomainEvents().forEach(eventPublisher::publishEvent);
    restaurant.clearEvents();
  }

  @Override
  public List<RestaurantImage> findImagesByRestaurantId(UUID restaurantId) {
    return this.restauranteImageJpaRepository.findByRestaurantId(restaurantId).stream()
        .map(imageMapper::toDomain)
        .toList();
  }

  @Override
  public void saveImage(RestaurantImage image) {
    this.restauranteImageJpaRepository.save(imageMapper.toEntity(image));
  }

  @Override
  public void deleteImage(UUID imageId) {
    this.restauranteImageJpaRepository.deleteById(imageId);
  }

  @Override
  public Optional<RestaurantImage> findImageById(UUID imageId) {
    return this.restauranteImageJpaRepository.findById(imageId).map(imageMapper::toDomain);
  }
}

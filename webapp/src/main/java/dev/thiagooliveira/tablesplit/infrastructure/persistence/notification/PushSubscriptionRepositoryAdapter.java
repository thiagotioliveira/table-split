package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PushSubscriptionRepositoryAdapter implements PushSubscriptionRepository {

  private final PushSubscriptionJpaRepository pushSubscriptionJpaRepository;
  private final PushSubscriptionEntityMapper mapper;

  public PushSubscriptionRepositoryAdapter(
      PushSubscriptionJpaRepository pushSubscriptionJpaRepository,
      PushSubscriptionEntityMapper mapper) {
    this.pushSubscriptionJpaRepository = pushSubscriptionJpaRepository;
    this.mapper = mapper;
  }

  @Override
  public PushSubscription save(PushSubscription subscription) {
    return mapper.toDomain(this.pushSubscriptionJpaRepository.save(mapper.toEntity(subscription)));
  }

  @Override
  public Optional<PushSubscription> findByEndpoint(String endpoint) {
    return this.pushSubscriptionJpaRepository.findByEndpoint(endpoint).map(mapper::toDomain);
  }

  @Override
  public List<PushSubscription> findAllByRestaurantId(UUID restaurantId) {
    return this.pushSubscriptionJpaRepository.findAllByRestaurantId(restaurantId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PushSubscription> findAllByUserIdAndRestaurantId(UUID userId, UUID restaurantId) {
    return this.pushSubscriptionJpaRepository
        .findAllByUserIdAndRestaurantId(userId, restaurantId)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PushSubscription> findAllByStaffIdAndRestaurantId(UUID staffId, UUID restaurantId) {
    return this.pushSubscriptionJpaRepository
        .findAllByStaffIdAndRestaurantId(staffId, restaurantId)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteByEndpoint(String endpoint) {
    this.pushSubscriptionJpaRepository.deleteByEndpoint(endpoint);
  }
}

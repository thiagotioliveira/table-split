package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.application.notification.PushSubscriptionRepository;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PushSubscriptionRepositoryAdapter implements PushSubscriptionRepository {

  private final PushSubscriptionJpaRepository pushSubscriptionJpaRepository;

  public PushSubscriptionRepositoryAdapter(
      PushSubscriptionJpaRepository pushSubscriptionJpaRepository) {
    this.pushSubscriptionJpaRepository = pushSubscriptionJpaRepository;
  }

  @Override
  public PushSubscription save(PushSubscription subscription) {
    return this.pushSubscriptionJpaRepository
        .save(PushSubscriptionEntity.fromDomain(subscription))
        .toDomain();
  }

  @Override
  public Optional<PushSubscription> findByEndpoint(String endpoint) {
    return this.pushSubscriptionJpaRepository
        .findByEndpoint(endpoint)
        .map(PushSubscriptionEntity::toDomain);
  }

  @Override
  public List<PushSubscription> findAllByRestaurantId(UUID restaurantId) {
    return this.pushSubscriptionJpaRepository.findAllByRestaurantId(restaurantId).stream()
        .map(PushSubscriptionEntity::toDomain)
        .toList();
  }

  @Override
  public List<PushSubscription> findAllByUserIdAndRestaurantId(UUID userId, UUID restaurantId) {
    return this.pushSubscriptionJpaRepository
        .findAllByUserIdAndRestaurantId(userId, restaurantId)
        .stream()
        .map(PushSubscriptionEntity::toDomain)
        .toList();
  }

  @Override
  public List<PushSubscription> findAllByStaffIdAndRestaurantId(UUID staffId, UUID restaurantId) {
    return this.pushSubscriptionJpaRepository
        .findAllByStaffIdAndRestaurantId(staffId, restaurantId)
        .stream()
        .map(PushSubscriptionEntity::toDomain)
        .toList();
  }

  @Override
  public void deleteByEndpoint(String endpoint) {
    this.pushSubscriptionJpaRepository.deleteByEndpoint(endpoint);
  }
}

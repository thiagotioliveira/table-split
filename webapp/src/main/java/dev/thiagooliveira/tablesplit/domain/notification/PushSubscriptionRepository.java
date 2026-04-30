package dev.thiagooliveira.tablesplit.domain.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushSubscriptionRepository {

  PushSubscription save(PushSubscription subscription);

  Optional<PushSubscription> findByEndpoint(String endpoint);

  List<PushSubscription> findAllByRestaurantId(UUID restaurantId);

  List<PushSubscription> findAllByUserIdAndRestaurantId(UUID userId, UUID restaurantId);

  List<PushSubscription> findAllByStaffIdAndRestaurantId(UUID staffId, UUID restaurantId);

  void deleteByEndpoint(String endpoint);
}

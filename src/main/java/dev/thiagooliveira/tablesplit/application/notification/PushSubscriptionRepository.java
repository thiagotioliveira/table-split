package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushSubscriptionRepository {

  PushSubscription save(PushSubscription subscription);

  Optional<PushSubscription> findByEndpoint(String endpoint);

  List<PushSubscription> findAllByRestaurantId(UUID restaurantId);

  void deleteByEndpoint(String endpoint);
}

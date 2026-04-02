package dev.thiagooliveira.tablesplit.domain.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

  List<PushSubscription> findAllByRestaurantId(UUID restaurantId);

  Optional<PushSubscription> findByEndpoint(String endpoint);

  void deleteByEndpoint(String endpoint);
}

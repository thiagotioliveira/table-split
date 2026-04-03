package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscriptionEntity, UUID> {

  List<PushSubscriptionEntity> findAllByRestaurantId(UUID restaurantId);

  Optional<PushSubscriptionEntity> findByEndpoint(String endpoint);

  void deleteByEndpoint(String endpoint);
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscriptionEntity, UUID> {

  List<PushSubscriptionEntity> findAllByRestaurantId(UUID restaurantId);

  Optional<PushSubscriptionEntity> findByEndpoint(String endpoint);

  List<PushSubscriptionEntity> findAllByUserIdAndRestaurantId(UUID userId, UUID restaurantId);

  List<PushSubscriptionEntity> findAllByStaffIdAndRestaurantId(UUID staffId, UUID restaurantId);

  void deleteByEndpoint(String endpoint);
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaiterCallJpaRepository extends JpaRepository<WaiterCallEntity, UUID> {
  List<WaiterCallEntity> findAllByRestaurantIdAndDismissedAtIsNullOrderByCreatedAtDesc(
      UUID restaurantId);
}

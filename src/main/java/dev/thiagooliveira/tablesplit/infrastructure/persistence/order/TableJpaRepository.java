package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableJpaRepository extends JpaRepository<TableEntity, UUID> {
  Optional<TableEntity> findByRestaurantIdAndCod(UUID restaurantId, String cod);

  java.util.List<TableEntity> findAllByRestaurantIdOrderByCod(UUID restaurantId);
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableJpaRepository extends JpaRepository<TableEntity, UUID> {

  /** Only active (non-deleted) tables. */
  Optional<TableEntity> findByRestaurantIdAndCodAndDeletedAtIsNull(UUID restaurantId, String cod);

  /** Includes soft-deleted — used to detect resurrection. */
  Optional<TableEntity> findByRestaurantIdAndCod(UUID restaurantId, String cod);

  List<TableEntity> findAllByRestaurantIdAndDeletedAtIsNullOrderByCod(UUID restaurantId);

  long countByRestaurantIdAndDeletedAtIsNull(UUID restaurantId);
}

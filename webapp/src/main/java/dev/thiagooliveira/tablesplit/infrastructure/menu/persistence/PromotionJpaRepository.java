package dev.thiagooliveira.tablesplit.infrastructure.menu.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionJpaRepository extends JpaRepository<PromotionEntity, UUID> {
  List<PromotionEntity> findByRestaurantId(UUID restaurantId);

  long countByRestaurantId(UUID restaurantId);
}

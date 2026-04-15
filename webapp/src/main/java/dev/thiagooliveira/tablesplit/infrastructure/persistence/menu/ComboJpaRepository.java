package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComboJpaRepository extends JpaRepository<ComboEntity, UUID> {
  List<ComboEntity> findByRestaurantId(UUID restaurantId);
}

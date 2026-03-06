package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, UUID> {

  @EntityGraph(attributePaths = "images")
  List<ItemEntity> findAllByCategoryRestaurantId(UUID restaurantId);
}

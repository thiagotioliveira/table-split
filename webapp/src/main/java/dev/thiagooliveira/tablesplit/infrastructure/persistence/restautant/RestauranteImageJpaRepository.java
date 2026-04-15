package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteImageJpaRepository extends JpaRepository<RestaurantImageEntity, UUID> {
  List<RestaurantImageEntity> findByRestaurantId(UUID restaurantId);
}

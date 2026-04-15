package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, UUID> {
  List<CouponEntity> findByRestaurantId(UUID restaurantId);

  Optional<CouponEntity> findByCodeAndRestaurantId(String code, UUID restaurantId);
}

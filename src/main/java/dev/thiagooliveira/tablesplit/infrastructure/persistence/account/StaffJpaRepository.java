package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffJpaRepository extends JpaRepository<StaffEntity, UUID> {
  Optional<StaffEntity> findByEmail(String email);

  List<StaffEntity> findByRestaurantId(UUID restaurantId);
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffJpaRepository extends JpaRepository<StaffEntity, UUID> {
  Optional<StaffEntity> findByEmail(String email);

  @org.springframework.data.jpa.repository.Query(
      "SELECT s FROM StaffEntity s WHERE REPLACE(s.phone, ' ', '') = REPLACE(:phone, ' ', '')")
  Optional<StaffEntity> findByPhone(
      @org.springframework.data.repository.query.Param("phone") String phone);

  List<StaffEntity> findByRestaurantId(UUID restaurantId);

  long countByRestaurantId(UUID restaurantId);
}

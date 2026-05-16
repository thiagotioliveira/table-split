package dev.thiagooliveira.tablesplit.infrastructure.notification.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscriptionEntity, UUID> {

  List<PushSubscriptionEntity> findAllByRestaurantId(UUID restaurantId);

  Optional<PushSubscriptionEntity> findByEndpoint(String endpoint);

  List<PushSubscriptionEntity> findAllByUserIdAndRestaurantId(UUID userId, UUID restaurantId);

  List<PushSubscriptionEntity> findAllByStaffIdAndRestaurantId(UUID staffId, UUID restaurantId);

  void deleteByEndpoint(String endpoint);

  @org.springframework.data.jpa.repository.Modifying
  @org.springframework.data.jpa.repository.Query(
      "UPDATE PushSubscriptionEntity p SET p.language = :language WHERE p.userId = :userId")
  void updateLanguageByUserId(
      @org.springframework.data.repository.query.Param("userId") UUID userId,
      @org.springframework.data.repository.query.Param("language") String language);

  @org.springframework.data.jpa.repository.Modifying
  @org.springframework.data.jpa.repository.Query(
      "UPDATE PushSubscriptionEntity p SET p.language = :language WHERE p.staffId = :staffId")
  void updateLanguageByStaffId(
      @org.springframework.data.repository.query.Param("staffId") UUID staffId,
      @org.springframework.data.repository.query.Param("language") String language);
}

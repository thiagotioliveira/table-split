package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderFeedbackJpaRepository extends JpaRepository<OrderFeedbackEntity, UUID> {
  boolean existsByOrder_IdAndCustomerId(UUID orderId, UUID customerId);

  java.util.List<OrderFeedbackEntity>
      findAllByOrder_RestaurantIdAndCreatedAtAfterOrderByCreatedAtDesc(
          UUID restaurantId, java.time.ZonedDateTime since);

  @org.springframework.data.jpa.repository.Query(
      "SELECT f.rating, COUNT(f) FROM OrderFeedbackEntity f WHERE f.order.restaurantId = :restaurantId AND f.createdAt > :since GROUP BY f.rating")
  java.util.List<Object[]> getRatingDistribution(UUID restaurantId, java.time.ZonedDateTime since);

  long countByOrder_RestaurantIdAndReadFalse(UUID restaurantId);

  @org.springframework.data.jpa.repository.Modifying
  @org.springframework.data.jpa.repository.Query(
      "UPDATE OrderFeedbackEntity f SET f.read = true WHERE f.order.restaurantId = :restaurantId AND f.read = false")
  void markAllAsReadByRestaurantId(UUID restaurantId);
}

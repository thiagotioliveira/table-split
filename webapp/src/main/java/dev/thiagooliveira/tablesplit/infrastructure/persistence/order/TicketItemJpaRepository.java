package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketItemJpaRepository extends JpaRepository<TicketItemEntity, UUID> {

  @Modifying
  @Query("UPDATE TicketItemEntity t SET t.rating = :rating WHERE t.id = :itemId")
  void updateRating(UUID itemId, Integer rating);

  @Query(
      "SELECT t.itemId, AVG(t.rating), COUNT(t.rating) FROM TicketItemEntity t "
          + "JOIN t.ticket ticket "
          + "WHERE ticket.order.restaurantId = :restaurantId AND ticket.createdAt > :since AND t.rating IS NOT NULL "
          + "GROUP BY t.itemId "
          + "HAVING AVG(t.rating) >= 4.0 "
          + "ORDER BY AVG(t.rating) DESC, COUNT(t.rating) DESC")
  org.springframework.data.domain.Page<Object[]> findTopRatedItems(
      UUID restaurantId,
      java.time.ZonedDateTime since,
      org.springframework.data.domain.Pageable pageable);

  @Query(
      "SELECT t.itemId, AVG(t.rating), COUNT(t.rating) FROM TicketItemEntity t "
          + "JOIN t.ticket ticket "
          + "WHERE ticket.order.restaurantId = :restaurantId AND ticket.createdAt > :since AND t.rating IS NOT NULL "
          + "GROUP BY t.itemId "
          + "HAVING AVG(t.rating) < 3.0 "
          + "ORDER BY AVG(t.rating) ASC, COUNT(t.rating) DESC")
  org.springframework.data.domain.Page<Object[]> findNeedAttentionItems(
      UUID restaurantId,
      java.time.ZonedDateTime since,
      org.springframework.data.domain.Pageable pageable);
}

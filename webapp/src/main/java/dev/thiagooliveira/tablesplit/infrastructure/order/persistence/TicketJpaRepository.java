package dev.thiagooliveira.tablesplit.infrastructure.order.persistence;

import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {
  long countByOrderRestaurantIdAndStatusAndCreatedAtBetween(
      UUID restaurantId, TicketStatus status, ZonedDateTime start, ZonedDateTime end);
}

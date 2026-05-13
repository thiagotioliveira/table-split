package dev.thiagooliveira.tablesplit.infrastructure.order.persistence;

import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {

  @Query(
      "select t from TicketEntity t join t.order o "
          + "where o.restaurantId = :restaurantId "
          + "and t.status in :statuses "
          + "and (cast(:start as timestamp) is null or t.createdAt >= :start) "
          + "and (cast(:end as timestamp) is null or t.createdAt <= :end) "
          + "order by t.createdAt desc")
  Page<TicketEntity> findHistory(
      UUID restaurantId,
      Collection<TicketStatus> statuses,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end,
      Pageable pageable);

  @Query(
      "select count(t) from TicketEntity t join t.order o "
          + "where o.restaurantId = :restaurantId "
          + "and t.status in :statuses "
          + "and (cast(:start as timestamp) is null or t.createdAt >= :start) "
          + "and (cast(:end as timestamp) is null or t.createdAt <= :end)")
  long countHistory(
      UUID restaurantId,
      Collection<TicketStatus> statuses,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end);

  @Query(
      "select sum(i.quantity * i.unitPrice) from TicketItemEntity i join i.ticket t join t.order o "
          + "where o.restaurantId = :restaurantId "
          + "and t.status in :statuses "
          + "and (cast(:start as timestamp) is null or t.createdAt >= :start) "
          + "and (cast(:end as timestamp) is null or t.createdAt <= :end) "
          + "and i.status <> 'CANCELLED'")
  Double sumRevenue(
      UUID restaurantId,
      Collection<TicketStatus> statuses,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end);

  long countByOrderRestaurantIdAndStatusAndCreatedAtBetween(
      UUID restaurantId,
      TicketStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end);
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
  Optional<OrderEntity> findByTableIdAndStatus(UUID tableId, OrderStatus status);

  List<OrderEntity> findAllByTableIdOrderByOpenedAtDesc(UUID tableId);

  List<OrderEntity> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status);

  @Query("select o from OrderEntity o join o.tickets t where t.id = :ticketId")
  Optional<OrderEntity> findByTicketId(UUID ticketId);
}

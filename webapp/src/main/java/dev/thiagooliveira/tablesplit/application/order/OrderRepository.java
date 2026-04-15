package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
  Optional<Order> findById(UUID id);

  Optional<Order> findActiveOrderByTableId(UUID tableId);

  List<Order> findAllByTableIdOrderByOpenedAtDesc(UUID tableId);

  List<Order> findAllByTableIdAndStatusAndOpenedAtBetween(
      UUID tableId, OrderStatus status, java.time.ZonedDateTime start, java.time.ZonedDateTime end);

  List<Order> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status);

  Optional<Order> findByTicketId(UUID ticketId);

  Optional<Order> findByTicketItemId(UUID itemId);

  void save(Order order);

  void delete(UUID id);
}

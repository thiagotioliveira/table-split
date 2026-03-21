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

  List<Order> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status);

  Optional<Order> findByTicketId(UUID ticketId);

  void save(Order order);
}

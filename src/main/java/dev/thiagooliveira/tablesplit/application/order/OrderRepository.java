package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
  Optional<Order> findById(UUID id);

  Optional<Order> findActiveOrderByTableId(UUID tableId);

  List<Order> findAllByTableIdOrderByOpenedAtDesc(UUID tableId);

  void save(Order order);
}

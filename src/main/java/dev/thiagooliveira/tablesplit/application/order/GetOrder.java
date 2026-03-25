package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetOrder {

  private final OrderRepository orderRepository;

  public GetOrder(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Optional<Order> execute(UUID tableId) {
    return orderRepository.findActiveOrderByTableId(tableId);
  }

  public List<Order> findAllByTableId(UUID tableId) {
    return orderRepository.findAllByTableIdOrderByOpenedAtDesc(tableId);
  }
}

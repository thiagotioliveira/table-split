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

  public Optional<Order> findById(UUID id) {
    return orderRepository.findById(id);
  }

  public List<Order> findAllByTableId(UUID tableId) {
    return orderRepository.findAllByTableIdOrderByOpenedAtDesc(tableId);
  }

  public List<Order> findAllFiltered(
      UUID tableId,
      dev.thiagooliveira.tablesplit.domain.order.OrderStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end) {
    return orderRepository.findAllByTableIdAndStatusAndOpenedAtBetween(tableId, status, start, end);
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetOrder {

  private final OrderRepository orderRepository;

  public GetOrder(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Optional<Order> execute(UUID tableId) {
    return orderRepository.findActiveOrderByTableId(tableId);
  }
}

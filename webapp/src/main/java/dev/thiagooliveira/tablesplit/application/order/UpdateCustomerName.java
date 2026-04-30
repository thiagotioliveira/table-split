package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import java.util.UUID;

public class UpdateCustomerName {

  private final OrderRepository orderRepository;

  public UpdateCustomerName(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public void execute(UUID tableId, UUID customerId, String newName) {
    Order order =
        orderRepository
            .findActiveOrderByTableId(tableId)
            .orElseThrow(() -> new IllegalStateException("No active order for table: " + tableId));

    order.updateCustomerName(customerId, newName);
    orderRepository.save(order);
  }
}

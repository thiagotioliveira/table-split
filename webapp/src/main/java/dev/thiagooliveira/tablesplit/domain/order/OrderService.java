package dev.thiagooliveira.tablesplit.domain.order;

import java.util.UUID;

public class OrderService {

  private final OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  /**
   * Business Rule: Check if the customer can start a new order on the table. If the customer was in
   * the last closed session, they might need to complete feedback first.
   */
  public void validateCustomerSession(UUID tableId, UUID customerId) {
    if (customerId == null) return;

    orderRepository.findAllByTableIdOrderByOpenedAtDesc(tableId).stream()
        .findFirst()
        .ifPresent(
            lastOrder -> {
              if (lastOrder.getStatus() == OrderStatus.CLOSED) {
                if (lastOrder.hasParticipant(customerId)) {
                  throw new TableSessionClosedException(
                      "Table is closed. Please complete your feedback before starting a new session.");
                }
              }
            });
  }
}

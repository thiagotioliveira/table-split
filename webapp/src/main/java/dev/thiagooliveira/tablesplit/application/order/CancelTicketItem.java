package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import java.util.UUID;

public class CancelTicketItem {

  private final OrderRepository orderRepository;

  public CancelTicketItem(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public void execute(UUID itemId, int quantityToCancel, String reason) {
    Order order =
        orderRepository
            .findByTicketItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

    UUID ticketId =
        order.getTickets().stream()
            .filter(t -> t.getItems().stream().anyMatch(i -> i.getId().equals(itemId)))
            .map(t -> t.getId())
            .findFirst()
            .orElseThrow();

    order.cancelTicketItem(ticketId, itemId, quantityToCancel, reason);

    orderRepository.save(order);
  }
}

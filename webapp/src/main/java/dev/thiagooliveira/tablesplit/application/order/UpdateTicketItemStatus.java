package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class UpdateTicketItemStatus {

  private final OrderRepository orderRepository;
  private final SyncTableStatus syncTableStatus;

  public UpdateTicketItemStatus(OrderRepository orderRepository, SyncTableStatus syncTableStatus) {
    this.orderRepository = orderRepository;
    this.syncTableStatus = syncTableStatus;
  }

  public void execute(UUID itemId, TicketStatus newStatus) {
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

    order.updateTicketItemStatus(ticketId, itemId, newStatus);

    orderRepository.save(order);
    syncTableStatus.execute(order);
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class MoveTicket {

  private final OrderRepository orderRepository;
  private final SyncTableStatus syncTableStatus;

  public MoveTicket(OrderRepository orderRepository, SyncTableStatus syncTableStatus) {
    this.orderRepository = orderRepository;
    this.syncTableStatus = syncTableStatus;
  }

  public void execute(UUID ticketId, TicketStatus newStatus) {
    Order order =
        orderRepository
            .findByTicketId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

    order.moveTicket(ticketId, newStatus);
    orderRepository.save(order);

    syncTableStatus.execute(order);
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class MoveTicket {

  private final OrderRepository orderRepository;

  public MoveTicket(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public void execute(UUID ticketId, TicketStatus newStatus) {
    Order order =
        orderRepository
            .findByTicketId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

    Ticket ticket =
        order.getTickets().stream()
            .filter(t -> t.getId().equals(ticketId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Ticket not found in order: " + ticketId));

    ticket.setStatus(newStatus);

    if (newStatus == TicketStatus.READY) {
      ticket.setReadyAt(java.time.ZonedDateTime.now());
    }

    orderRepository.save(order);
  }
}

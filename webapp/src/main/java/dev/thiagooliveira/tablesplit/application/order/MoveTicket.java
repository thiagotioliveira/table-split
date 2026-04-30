package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class MoveTicket {

  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;
  private final SyncTableStatus syncTableStatus;

  public MoveTicket(
      OrderRepository orderRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus) {
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
    this.syncTableStatus = syncTableStatus;
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
      ticket.setReadyAt(Time.now());
    }

    orderRepository.save(order);
    eventPublisher.publishEvent(new TicketStatusChangedEvent(order, ticket, newStatus));

    syncTableStatus.execute(order);
  }
}

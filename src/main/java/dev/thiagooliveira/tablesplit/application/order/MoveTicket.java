package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class MoveTicket {

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;
  private final EventPublisher eventPublisher;

  public MoveTicket(
      OrderRepository orderRepository,
      TableRepository tableRepository,
      EventPublisher eventPublisher) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
    this.eventPublisher = eventPublisher;
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
    eventPublisher.publishEvent(new TicketStatusChangedEvent(order, ticket, newStatus));

    // Check table status
    Table table =
        tableRepository
            .findById(order.getTableId())
            .orElseThrow(() -> new IllegalStateException("Table not found: " + order.getTableId()));

    boolean hasWaiting =
        order.getTickets().stream()
            .anyMatch(
                t ->
                    t.getStatus() == TicketStatus.PENDING
                        || t.getStatus() == TicketStatus.PREPARING
                        || t.getStatus() == TicketStatus.READY);

    if (hasWaiting) {
      if (table.getStatus() != TableStatus.WAITING) {
        table.waiting();
        tableRepository.save(table);
        eventPublisher.publishEvent(new TableStatusChangedEvent(table));
      }
    } else {
      // If no waiting tickets, and it's not available, it must be occupied
      if (table.getStatus() != TableStatus.OCCUPIED) {
        table.occupy();
        tableRepository.save(table);
        eventPublisher.publishEvent(new TableStatusChangedEvent(table));
      }
    }
  }
}

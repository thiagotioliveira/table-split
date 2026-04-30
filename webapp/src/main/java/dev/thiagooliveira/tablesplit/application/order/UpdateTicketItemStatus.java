package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.TicketItemStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class UpdateTicketItemStatus {

  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;
  private final SyncTableStatus syncTableStatus;

  public UpdateTicketItemStatus(
      OrderRepository orderRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus) {
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
    this.syncTableStatus = syncTableStatus;
  }

  public void execute(UUID itemId, TicketStatus newStatus) {
    Order order =
        orderRepository
            .findByTicketItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

    order
        .getTickets()
        .forEach(
            ticket -> {
              ticket.getItems().stream()
                  .filter(item -> item.getId().equals(itemId))
                  .findFirst()
                  .ifPresent(
                      item -> {
                        TicketStatus oldTicketStatus = ticket.getStatus();
                        item.setStatus(newStatus);
                        ticket.recalculateStatus();

                        eventPublisher.publishEvent(
                            new TicketItemStatusChangedEvent(order, ticket, item, newStatus));

                        if (ticket.getStatus() != oldTicketStatus) {
                          eventPublisher.publishEvent(
                              new TicketStatusChangedEvent(order, ticket, ticket.getStatus()));
                        }
                      });
            });

    orderRepository.save(order);
    syncTableStatus.execute(order);
  }
}

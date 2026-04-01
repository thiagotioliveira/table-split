package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.TicketItemStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class CancelTicketItem {

  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;

  public CancelTicketItem(OrderRepository orderRepository, EventPublisher eventPublisher) {
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID itemId, int quantityToCancel, String reason) {
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
                        if (quantityToCancel >= item.getQuantity()) {
                          item.setStatus(TicketStatus.CANCELLED);
                          if (reason != null && !reason.isBlank()) {
                            String note = item.getNote() != null ? item.getNote() : "";
                            item.setNote(note + " (Cancelado: " + reason + ")");
                          }
                          eventPublisher.publishEvent(
                              new TicketItemStatusChangedEvent(
                                  order, ticket, item, TicketStatus.CANCELLED));
                        } else {
                          // Partial cancellation
                          int remainingQty = item.getQuantity() - quantityToCancel;
                          item.setQuantity(remainingQty);

                          TicketItem cancelledItem = new TicketItem();
                          cancelledItem.setId(UUID.randomUUID());
                          cancelledItem.setItemId(item.getItemId());
                          cancelledItem.setName(item.getName());
                          cancelledItem.setCustomerId(item.getCustomerId());
                          cancelledItem.setQuantity(quantityToCancel);
                          cancelledItem.setUnitPrice(item.getUnitPrice());
                          cancelledItem.setStatus(TicketStatus.CANCELLED);

                          String cancelNote = "(Cancelado " + quantityToCancel + "x";
                          if (reason != null && !reason.isBlank()) {
                            cancelNote += ": " + reason;
                          }
                          cancelNote += ")";
                          cancelledItem.setNote(cancelNote);

                          ticket.getItems().add(cancelledItem);
                          eventPublisher.publishEvent(
                              new TicketItemStatusChangedEvent(
                                  order, ticket, cancelledItem, TicketStatus.CANCELLED));
                        }
                        ticket.recalculateStatus();
                        if (ticket.getStatus() != oldTicketStatus) {
                          eventPublisher.publishEvent(
                              new TicketStatusChangedEvent(order, ticket, ticket.getStatus()));
                        }
                      });
            });

    orderRepository.save(order);
  }
}

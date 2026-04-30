package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class TicketStatusChangedEvent implements DomainEvent {
  private final UUID restaurantId;
  private final UUID tableId;
  private final UUID orderId;
  private final UUID ticketId;
  private final TicketStatus newStatus;
  private final Order order;

  public TicketStatusChangedEvent(Order order, Ticket ticket, TicketStatus newStatus) {
    this.restaurantId = order.getRestaurantId();
    this.tableId = order.getTableId();
    this.orderId = order.getId();
    this.ticketId = ticket.getId();
    this.newStatus = newStatus;
    this.order = order;
  }

  @Override
  public UUID getAccountId() {
    return order != null ? order.getAccountId() : null;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public UUID getTableId() {
    return tableId;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getTicketId() {
    return ticketId;
  }

  public TicketStatus getNewStatus() {
    return newStatus;
  }

  public Order getOrder() {
    return order;
  }
}

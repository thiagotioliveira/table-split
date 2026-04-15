package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;

public class TicketItemStatusChangedEvent implements DomainEvent {
  private final UUID restaurantId;
  private final UUID tableId;
  private final UUID orderId;
  private final UUID ticketId;
  private final UUID itemId;
  private final TicketStatus newStatus;
  private final Order order;

  public TicketItemStatusChangedEvent(
      Order order, Ticket ticket, TicketItem item, TicketStatus newStatus) {
    this.restaurantId = order.getRestaurantId();
    this.tableId = order.getTableId();
    this.orderId = order.getId();
    this.ticketId = ticket.getId();
    this.itemId = item.getId();
    this.newStatus = newStatus;
    this.order = order;
  }

  @Override
  public UUID getAccountId() {
    return null;
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

  public UUID getItemId() {
    return itemId;
  }

  public TicketStatus getNewStatus() {
    return newStatus;
  }

  public Order getOrder() {
    return order;
  }
}

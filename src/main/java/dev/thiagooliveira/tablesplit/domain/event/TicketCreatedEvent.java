package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import java.util.UUID;

public class TicketCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final UUID tableId;
  private final UUID orderId;
  private final UUID ticketId;

  public TicketCreatedEvent(Order order, Ticket ticket) {
    this.accountId = null;
    this.restaurantId = order.getRestaurantId();
    this.tableId = order.getTableId();
    this.orderId = order.getId();
    this.ticketId = ticket.getId();
  }

  @Override
  public UUID getAccountId() {
    return accountId;
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
}

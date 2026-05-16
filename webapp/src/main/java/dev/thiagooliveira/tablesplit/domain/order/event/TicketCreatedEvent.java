package dev.thiagooliveira.tablesplit.domain.order.event;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import java.util.UUID;

public class TicketCreatedEvent implements DomainEvent {
  private final UUID restaurantId;
  private final UUID tableId;
  private final String tableCod;
  private final UUID orderId;
  private final Ticket ticket;
  private final Order order;
  private final UUID initiatedBy;
  private final Language language;

  public TicketCreatedEvent(
      Order order, Ticket ticket, String tableCod, UUID initiatedBy, Language language) {
    this.restaurantId = order.getRestaurantId();
    this.tableId = order.getTableId();
    this.tableCod = tableCod;
    this.orderId = order.getId();
    this.ticket = ticket;
    this.order = order;
    this.initiatedBy = initiatedBy;
    this.language = language;
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

  public Ticket getTicket() {
    return ticket;
  }

  public String getTableCod() {
    return tableCod;
  }

  public Order getOrder() {
    return order;
  }

  public UUID getInitiatedBy() {
    return initiatedBy;
  }

  public Language getLanguage() {
    return language;
  }
}

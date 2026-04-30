package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class TableOpenedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final UUID tableId;
  private final UUID orderId;

  public TableOpenedEvent(Order order, Table table) {
    this.accountId = order.getAccountId();
    this.restaurantId = order.getRestaurantId();
    this.tableId = table.getId();
    this.orderId = order.getId();
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
}

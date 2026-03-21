package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class TableCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final UUID tableId;

  public TableCreatedEvent(Table table) {
    this.accountId = null;
    this.restaurantId = table.getRestaurantId();
    this.tableId = table.getId();
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
}

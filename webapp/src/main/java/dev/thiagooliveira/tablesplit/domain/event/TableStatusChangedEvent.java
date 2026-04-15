package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.UUID;

public class TableStatusChangedEvent implements DomainEvent {
  private final UUID restaurantId;
  private final UUID tableId;
  private final TableStatus status;

  public TableStatusChangedEvent(Table table) {
    this.restaurantId = table.getRestaurantId();
    this.tableId = table.getId();
    this.status = table.getStatus();
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

  public TableStatus getStatus() {
    return status;
  }
}

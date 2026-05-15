package dev.thiagooliveira.tablesplit.domain.order.event;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class TableTransferredEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final UUID orderId;
  private final UUID sourceTableId;
  private final UUID targetTableId;
  private final String sourceTableCod;
  private final String targetTableCod;

  public TableTransferredEvent(Order order, Table sourceTable, Table targetTable) {
    this.accountId = order.getAccountId();
    this.restaurantId = order.getRestaurantId();
    this.orderId = order.getId();
    this.sourceTableId = sourceTable.getId();
    this.targetTableId = targetTable.getId();
    this.sourceTableCod = sourceTable.getCod();
    this.targetTableCod = targetTable.getCod();
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getSourceTableId() {
    return sourceTableId;
  }

  public UUID getTargetTableId() {
    return targetTableId;
  }

  public String getSourceTableCod() {
    return sourceTableCod;
  }

  public String getTargetTableCod() {
    return targetTableCod;
  }
}

package dev.thiagooliveira.tablesplit.domain.order;

import java.util.UUID;

public class OverpaymentException extends RuntimeException {
  private final UUID tableId;

  public OverpaymentException(UUID tableId) {
    this.tableId = tableId;
  }

  public UUID getTableId() {
    return tableId;
  }
}

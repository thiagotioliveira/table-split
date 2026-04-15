package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.DomainException;
import java.util.UUID;

public class OverpaymentException extends DomainException {
  private final UUID tableId;

  public OverpaymentException(UUID tableId) {
    super("Overpayment detected on table: " + tableId);
    this.tableId = tableId;
  }

  public UUID getTableId() {
    return tableId;
  }
}

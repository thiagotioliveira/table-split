package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.DomainException;
import java.util.UUID;

public class IllegalOrderStatusException extends DomainException {
  public enum Reason {
    PAYMENT_NOT_ALLOWED,
    PAYMENT_REMOVAL_NOT_ALLOWED,
    TICKET_NOT_ALLOWED,
    CLOSE_NOT_ALLOWED
  }

  private final UUID tableId;
  private final Reason reason;

  public IllegalOrderStatusException(UUID tableId, Reason reason) {
    super("Illegal order status: " + reason);
    this.tableId = tableId;
    this.reason = reason;
  }

  public UUID getTableId() {
    return tableId;
  }

  public Reason getReason() {
    return reason;
  }
}

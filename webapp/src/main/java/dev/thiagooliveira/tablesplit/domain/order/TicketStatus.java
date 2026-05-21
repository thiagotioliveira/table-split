package dev.thiagooliveira.tablesplit.domain.order;

public enum TicketStatus {
  PENDING,
  PREPARING,
  READY,
  DELIVERED,
  CANCELLED;

  public boolean isPending() {
    return this == PENDING;
  }

  public boolean isPreparing() {
    return this == PREPARING;
  }

  public boolean isReady() {
    return this == READY;
  }

  public boolean isDelivered() {
    return this == DELIVERED;
  }

  public boolean isCancelled() {
    return this == CANCELLED;
  }
}

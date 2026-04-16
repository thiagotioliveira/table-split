package dev.thiagooliveira.tablesplit.domain.order;

public enum TicketStatus {
  PENDING("Pendente", "pending"),
  PREPARING("Preparando", "preparing"),
  READY("Pronto", "ready"),
  DELIVERED("Entregue", "delivered"),
  CANCELLED("Cancelado", "cancelled");

  private final String label;
  private final String cssClass;

  TicketStatus(String label, String cssClass) {
    this.label = label;
    this.cssClass = cssClass;
  }

  public String getLabel() {
    return label;
  }

  public String getCssClass() {
    return cssClass;
  }

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

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
}

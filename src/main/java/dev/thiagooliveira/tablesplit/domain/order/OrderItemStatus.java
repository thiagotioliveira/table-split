package dev.thiagooliveira.tablesplit.domain.order;

public enum OrderItemStatus {
  PENDING("Pendente", "pending"),
  PREPARING("Preparando", "preparing"),
  READY("Pronto", "ready"),
  DELIVERED("Entregue", "delivered");

  private final String label;
  private final String cssClass;

  OrderItemStatus(String label, String cssClass) {
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

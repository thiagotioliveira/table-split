package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import java.math.BigDecimal;
import java.util.UUID;

public class TicketItemModel {
  private final UUID id;
  private final String customerName;
  private final String name;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal totalPrice;
  private final String note;
  private final String status;
  private final String statusLabel;
  private final String statusClass;

  public TicketItemModel(TicketItem item, String customerName) {
    this.id = item.getId();
    this.customerName = customerName;
    this.name =
        item.getName().get(Language.PT); // Default to PT or should use a preferred language?
    this.quantity = item.getQuantity();
    this.unitPrice = item.getUnitPrice();
    this.totalPrice = item.getTotalPrice();
    this.note = item.getNote();
    this.status = item.getStatus().name().toLowerCase();
    this.statusLabel = item.getStatus().getLabel();
    this.statusClass = item.getStatus().getCssClass();
  }

  public UUID getId() {
    return id;
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public String getNote() {
    return note;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusLabel() {
    return statusLabel;
  }

  public String getStatusClass() {
    return statusClass;
  }
}

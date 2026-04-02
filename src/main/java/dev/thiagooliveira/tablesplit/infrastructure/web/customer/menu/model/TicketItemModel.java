package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TicketItemModel {
  private final String id;
  private final String customerId;
  private final String customerName;
  private final String name;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal totalPrice;
  private final String note;
  private final String status;
  private final String statusLabel;
  private final String statusClass;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private final ZonedDateTime createdAt;

  public TicketItemModel(TicketItem item, String customerName, ZonedDateTime createdAt) {
    this.id = item.getId().toString();
    this.customerId = item.getCustomerId().toString();
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
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
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

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }
}

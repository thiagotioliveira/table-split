package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;

public class OrderItemModel {
  private final String customerName;
  private final String name;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal totalPrice;
  private final String note;
  private final String status;
  private final String statusClass;

  public OrderItemModel(
      String customerName,
      String name,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note,
      String status,
      String statusClass) {
    this.customerName = customerName;
    this.name = name;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
    this.note = note;
    this.status = status;
    this.statusClass = statusClass;
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

  public String getStatusClass() {
    return statusClass;
  }
}

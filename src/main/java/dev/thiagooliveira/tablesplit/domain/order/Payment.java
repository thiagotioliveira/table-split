package dev.thiagooliveira.tablesplit.domain.order;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Payment {
  private UUID id;
  private UUID orderId;
  private String customerName;
  private BigDecimal amount;
  private ZonedDateTime paidAt;

  private PaymentMethod method;
  private String note;

  public Payment() {}

  public Payment(
      UUID id,
      UUID orderId,
      String customerName,
      BigDecimal amount,
      PaymentMethod method,
      String note) {
    this.id = id;
    this.orderId = orderId;
    this.customerName = customerName;
    this.amount = amount;
    this.method = method;
    this.note = note;
    this.paidAt = ZonedDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public ZonedDateTime getPaidAt() {
    return paidAt;
  }

  public void setPaidAt(ZonedDateTime paidAt) {
    this.paidAt = paidAt;
  }

  public PaymentMethod getMethod() {
    return method;
  }

  public void setMethod(PaymentMethod method) {
    this.method = method;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}

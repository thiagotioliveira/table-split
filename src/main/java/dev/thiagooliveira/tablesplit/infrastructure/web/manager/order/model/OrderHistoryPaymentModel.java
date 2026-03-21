package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;

public class OrderHistoryPaymentModel {
  private final String id;
  private final String customerName;
  private final BigDecimal amount;
  private final String paidAt;
  private final String method;
  private final String note;

  public OrderHistoryPaymentModel(
      String id,
      String customerName,
      BigDecimal amount,
      String paidAt,
      String method,
      String note) {
    this.id = id;
    this.customerName = customerName;
    this.amount = amount;
    this.paidAt = paidAt;
    this.method = method;
    this.note = note;
  }

  public String getId() {
    return id;
  }

  public String getCustomerName() {
    return customerName;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getPaidAt() {
    return paidAt;
  }

  public String getMethod() {
    return method;
  }

  public String getNote() {
    return note;
  }
}

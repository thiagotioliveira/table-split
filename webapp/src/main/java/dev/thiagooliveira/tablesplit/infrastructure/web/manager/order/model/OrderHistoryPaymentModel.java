package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class OrderHistoryPaymentModel {
  private final String id;
  private final UUID customerId;
  private final BigDecimal amount;
  private final ZonedDateTime paidAt;
  private final String method;
  private final String note;

  public OrderHistoryPaymentModel(
      String id,
      UUID customerId,
      BigDecimal amount,
      ZonedDateTime paidAt,
      String method,
      String note) {
    this.id = id;
    this.customerId = customerId;
    this.amount = amount;
    this.paidAt = paidAt;
    this.method = method;
    this.note = note;
  }

  public String getId() {
    return id;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public ZonedDateTime getPaidAt() {
    return paidAt;
  }

  public String getMethod() {
    return method;
  }

  public String getNote() {
    return note;
  }
}

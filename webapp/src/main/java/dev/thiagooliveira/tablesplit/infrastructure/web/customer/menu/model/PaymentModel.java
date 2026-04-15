package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentModel {
  private final String id;
  private final String customerId;
  private final BigDecimal amount;
  private final String method;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private final ZonedDateTime paidAt;

  public PaymentModel(dev.thiagooliveira.tablesplit.domain.order.Payment payment) {
    this.id = payment.getId().toString();
    this.customerId = payment.getCustomerId() != null ? payment.getCustomerId().toString() : null;
    this.amount = payment.getAmount();
    this.method = payment.getMethod().name();
    this.paidAt = payment.getPaidAt();
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getMethod() {
    return method;
  }

  public ZonedDateTime getPaidAt() {
    return paidAt;
  }
}

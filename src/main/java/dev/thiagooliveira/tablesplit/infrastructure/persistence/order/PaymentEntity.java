package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.domain.order.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {

  @Id private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(name = "paid_at", nullable = false)
  private ZonedDateTime paidAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod method;

  @Column private String note;

  public PaymentEntity() {}

  public static PaymentEntity fromDomain(Payment payment) {
    PaymentEntity entity = new PaymentEntity();
    entity.setId(payment.getId());
    entity.setOrderId(payment.getOrderId());
    entity.setCustomerId(payment.getCustomerId());
    entity.setAmount(payment.getAmount());
    entity.setPaidAt(payment.getPaidAt());
    entity.setMethod(payment.getMethod());
    entity.setNote(payment.getNote());
    return entity;
  }

  public Payment toDomain() {
    Payment payment = new Payment();
    payment.setId(this.id);
    payment.setOrderId(this.orderId);
    payment.setCustomerId(this.customerId);
    payment.setAmount(this.amount);
    payment.setPaidAt(this.paidAt);
    payment.setMethod(this.method);
    payment.setNote(this.note);
    return payment;
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

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
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

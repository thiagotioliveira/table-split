package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "order_feedbacks")
public class OrderFeedbackEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @Column(nullable = false)
  private UUID customerId;

  @Column(nullable = false)
  private Integer rating;

  @Column(length = 500)
  private String comment;

  @Column(nullable = false)
  private ZonedDateTime createdAt;

  public OrderFeedbackEntity() {}

  public OrderFeedback toDomain() {
    OrderFeedback domain = new OrderFeedback();
    domain.setId(this.id);
    domain.setOrderId(this.order != null ? this.order.getId() : null);
    domain.setCustomerId(this.customerId);
    domain.setRating(this.rating);
    domain.setComment(this.comment);
    domain.setCreatedAt(this.createdAt);
    return domain;
  }

  public static OrderFeedbackEntity fromDomain(OrderFeedback domain) {
    OrderFeedbackEntity entity = new OrderFeedbackEntity();
    entity.setId(domain.getId());
    entity.setOrder(new OrderEntity());
    entity.getOrder().setId(domain.getOrderId());
    entity.setCustomerId(domain.getCustomerId());
    entity.setRating(domain.getRating());
    entity.setComment(domain.getComment());
    entity.setCreatedAt(domain.getCreatedAt());
    return entity;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OrderEntity getOrder() {
    return order;
  }

  public void setOrder(OrderEntity order) {
    this.order = order;
  }

  public UUID getOrderId() {
    return order != null ? order.getId() : null;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }
}

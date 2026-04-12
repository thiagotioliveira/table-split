package dev.thiagooliveira.tablesplit.domain.order;

import java.time.ZonedDateTime;
import java.util.UUID;

public class OrderFeedback {
  private UUID id;
  private UUID orderId;
  private UUID customerId;
  private Integer rating;
  private String comment;
  private ZonedDateTime createdAt;

  public OrderFeedback() {}

  public OrderFeedback(UUID id, UUID orderId, UUID customerId, Integer rating, String comment) {
    this.id = id;
    this.orderId = orderId;
    this.customerId = customerId;
    this.rating = rating;
    this.comment = comment;
    this.createdAt = ZonedDateTime.now();
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

package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "waiter_calls")
public class WaiterCallEntity {
  @Id private UUID id;
  private UUID restaurantId;
  private String tableCod;
  private ZonedDateTime createdAt;
  private ZonedDateTime dismissedAt;
  private Integer callCount;

  public WaiterCallEntity() {}

  public WaiterCallEntity(
      UUID id,
      UUID restaurantId,
      String tableCod,
      ZonedDateTime createdAt,
      ZonedDateTime dismissedAt,
      Integer callCount) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.createdAt = createdAt;
    this.dismissedAt = dismissedAt;
    this.callCount = callCount;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getTableCod() {
    return tableCod;
  }

  public void setTableCod(String tableCod) {
    this.tableCod = tableCod;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ZonedDateTime getDismissedAt() {
    return dismissedAt;
  }

  public void setDismissedAt(ZonedDateTime dismissedAt) {
    this.dismissedAt = dismissedAt;
  }

  public Integer getCallCount() {
    return callCount;
  }

  public void setCallCount(Integer callCount) {
    this.callCount = callCount;
  }
}

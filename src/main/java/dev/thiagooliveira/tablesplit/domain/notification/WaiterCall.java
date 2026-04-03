package dev.thiagooliveira.tablesplit.domain.notification;

import java.time.ZonedDateTime;
import java.util.UUID;

public class WaiterCall {
  private final UUID id;
  private final UUID restaurantId;
  private final String tableCod;
  private final ZonedDateTime createdAt;
  private ZonedDateTime dismissedAt;

  public WaiterCall(UUID id, UUID restaurantId, String tableCod, ZonedDateTime createdAt) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.createdAt = createdAt;
  }

  public WaiterCall(
      UUID id,
      UUID restaurantId,
      String tableCod,
      ZonedDateTime createdAt,
      ZonedDateTime dismissedAt) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.createdAt = createdAt;
    this.dismissedAt = dismissedAt;
  }

  public void dismiss() {
    this.dismissedAt = ZonedDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public String getTableCod() {
    return tableCod;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTime getDismissedAt() {
    return dismissedAt;
  }

  public boolean isDismissed() {
    return dismissedAt != null;
  }
}

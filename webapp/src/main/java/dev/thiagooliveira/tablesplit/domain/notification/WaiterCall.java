package dev.thiagooliveira.tablesplit.domain.notification;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.ZonedDateTime;
import java.util.UUID;

public class WaiterCall {
  private final UUID id;
  private final UUID restaurantId;
  private final String tableCod;
  private final ZonedDateTime createdAt;
  private ZonedDateTime dismissedAt;
  private int callCount;

  public WaiterCall(UUID id, UUID restaurantId, String tableCod, ZonedDateTime createdAt) {
    this(id, restaurantId, tableCod, createdAt, null, 1);
  }

  public WaiterCall(
      UUID id,
      UUID restaurantId,
      String tableCod,
      ZonedDateTime createdAt,
      ZonedDateTime dismissedAt) {
    this(id, restaurantId, tableCod, createdAt, dismissedAt, 1);
  }

  public WaiterCall(
      UUID id,
      UUID restaurantId,
      String tableCod,
      ZonedDateTime createdAt,
      ZonedDateTime dismissedAt,
      int callCount) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.createdAt = createdAt;
    this.dismissedAt = dismissedAt;
    this.callCount = callCount;
  }

  public void incrementCount() {
    if (!isDismissed()) {
      this.callCount++;
    }
  }

  public void dismiss() {
    this.dismissedAt = Time.now();
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

  public int getCallCount() {
    return callCount;
  }
}

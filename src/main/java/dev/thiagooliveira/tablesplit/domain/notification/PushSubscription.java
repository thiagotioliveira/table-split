package dev.thiagooliveira.tablesplit.domain.notification;

import java.time.ZonedDateTime;
import java.util.UUID;

public class PushSubscription {

  private UUID id;
  private UUID restaurantId;
  private String endpoint;
  private String p256dh;
  private String auth;
  private boolean notifyNewOrders = true;
  private boolean notifyCallWaiter = true;
  private ZonedDateTime createdAt;

  public PushSubscription() {}

  public PushSubscription(UUID restaurantId, String endpoint, String p256dh, String auth) {
    this(UUID.randomUUID(), restaurantId, endpoint, p256dh, auth);
  }

  public PushSubscription(UUID id, UUID restaurantId, String endpoint, String p256dh, String auth) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.endpoint = endpoint;
    this.p256dh = p256dh;
    this.auth = auth;
    this.notifyNewOrders = true;
    this.notifyCallWaiter = true;
    this.createdAt = ZonedDateTime.now();
  }

  public PushSubscription(
      UUID id,
      UUID restaurantId,
      String endpoint,
      String p256dh,
      String auth,
      boolean notifyNewOrders,
      boolean notifyCallWaiter) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.endpoint = endpoint;
    this.p256dh = p256dh;
    this.auth = auth;
    this.notifyNewOrders = notifyNewOrders;
    this.notifyCallWaiter = notifyCallWaiter;
    this.createdAt = ZonedDateTime.now();
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

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getP256dh() {
    return p256dh;
  }

  public void setP256dh(String p256dh) {
    this.p256dh = p256dh;
  }

  public String getAuth() {
    return auth;
  }

  public void setAuth(String auth) {
    this.auth = auth;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isNotifyNewOrders() {
    return notifyNewOrders;
  }

  public void setNotifyNewOrders(boolean notifyNewOrders) {
    this.notifyNewOrders = notifyNewOrders;
  }

  public boolean isNotifyCallWaiter() {
    return notifyCallWaiter;
  }

  public void setNotifyCallWaiter(boolean notifyCallWaiter) {
    this.notifyCallWaiter = notifyCallWaiter;
  }
}

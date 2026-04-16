package dev.thiagooliveira.tablesplit.domain.notification;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PushSubscription {

  private UUID id;
  private UUID restaurantId;
  private UUID userId;
  private UUID staffId;
  private String endpoint;
  private String p256dh;
  private String auth;
  private boolean notifyNewOrders = true;
  private boolean notifyCallWaiter = true;
  private ZonedDateTime createdAt;

  public PushSubscription() {}

  public static PushSubscription forUser(
      UUID restaurantId, UUID userId, String endpoint, String p256dh, String auth) {
    PushSubscription sub = new PushSubscription();
    sub.id = UUID.randomUUID();
    sub.restaurantId = restaurantId;
    sub.userId = userId;
    sub.endpoint = endpoint;
    sub.p256dh = p256dh;
    sub.auth = auth;
    sub.createdAt = Time.now();
    return sub;
  }

  public static PushSubscription forStaff(
      UUID restaurantId, UUID staffId, String endpoint, String p256dh, String auth) {
    PushSubscription sub = new PushSubscription();
    sub.id = UUID.randomUUID();
    sub.restaurantId = restaurantId;
    sub.staffId = staffId;
    sub.endpoint = endpoint;
    sub.p256dh = p256dh;
    sub.auth = auth;
    sub.createdAt = Time.now();
    return sub;
  }

  public PushSubscription(
      UUID id,
      UUID restaurantId,
      UUID userId,
      String endpoint,
      String p256dh,
      String auth,
      boolean notifyNewOrders,
      boolean notifyCallWaiter) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.userId = userId;
    this.endpoint = endpoint;
    this.p256dh = p256dh;
    this.auth = auth;
    this.notifyNewOrders = notifyNewOrders;
    this.notifyCallWaiter = notifyCallWaiter;
    this.createdAt = Time.now();
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

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
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

  public UUID getStaffId() {
    return staffId;
  }

  public void setStaffId(UUID staffId) {
    this.staffId = staffId;
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

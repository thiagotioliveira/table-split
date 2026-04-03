package dev.thiagooliveira.tablesplit.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "push_subscriptions")
public class PushSubscription {

  @Id private UUID id;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(nullable = false, unique = true, length = 512)
  private String endpoint;

  @Column(nullable = false)
  private String p256dh;

  @Column(nullable = false)
  private String auth;

  @Column(name = "notify_new_orders", nullable = false)
  private boolean notifyNewOrders = true;

  @Column(name = "notify_call_waiter", nullable = false)
  private boolean notifyCallWaiter = true;

  @Column(name = "created_at")
  private ZonedDateTime createdAt;

  protected PushSubscription() {}

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

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getP256dh() {
    return p256dh;
  }

  public String getAuth() {
    return auth;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
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

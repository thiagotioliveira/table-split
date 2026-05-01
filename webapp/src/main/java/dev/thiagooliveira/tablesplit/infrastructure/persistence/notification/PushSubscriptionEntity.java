package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "push_subscriptions")
public class PushSubscriptionEntity {

  @Id private UUID id;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "staff_id")
  private UUID staffId;

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

  protected PushSubscriptionEntity() {}

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

  public UUID getStaffId() {
    return staffId;
  }

  public void setStaffId(UUID staffId) {
    this.staffId = staffId;
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

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
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

  public PushSubscription toDomain() {
    PushSubscription domain = new PushSubscription();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setEndpoint(this.endpoint);
    domain.setP256dh(this.p256dh);
    domain.setAuth(this.auth);
    domain.setNotifyNewOrders(this.notifyNewOrders);
    domain.setNotifyCallWaiter(this.notifyCallWaiter);
    domain.setCreatedAt(this.createdAt);
    return domain;
  }

  public static PushSubscriptionEntity fromDomain(PushSubscription domain) {
    PushSubscriptionEntity entity = new PushSubscriptionEntity();
    entity.id = domain.getId();
    entity.restaurantId = domain.getRestaurantId();
    entity.endpoint = domain.getEndpoint();
    entity.p256dh = domain.getP256dh();
    entity.auth = domain.getAuth();
    entity.notifyNewOrders = domain.isNotifyNewOrders();
    entity.notifyCallWaiter = domain.isNotifyCallWaiter();
    entity.createdAt = domain.getCreatedAt() != null ? domain.getCreatedAt() : ZonedDateTime.now();
    return entity;
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

  public boolean isNotifyNewOrders() {
    return notifyNewOrders;
  }

  public boolean isNotifyCallWaiter() {
    return notifyCallWaiter;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }
}

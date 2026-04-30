package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import java.util.UUID;

public class Subscribe {

  private final PushSubscriptionRepository repository;

  public Subscribe(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  public void executeForUser(
      UUID restaurantId, UUID userId, String endpoint, String p256dh, String auth) {
    repository
        .findByEndpoint(endpoint)
        .ifPresentOrElse(
            existing -> {
              PushSubscription sub =
                  PushSubscription.forUser(restaurantId, userId, endpoint, p256dh, auth);
              sub.setId(existing.getId());
              sub.setNotifyNewOrders(existing.isNotifyNewOrders());
              sub.setNotifyCallWaiter(existing.isNotifyCallWaiter());
              repository.save(sub);
            },
            () ->
                repository.save(
                    PushSubscription.forUser(restaurantId, userId, endpoint, p256dh, auth)));
  }

  public void executeForStaff(
      UUID restaurantId, UUID staffId, String endpoint, String p256dh, String auth) {
    repository
        .findByEndpoint(endpoint)
        .ifPresentOrElse(
            existing -> {
              PushSubscription sub =
                  PushSubscription.forStaff(restaurantId, staffId, endpoint, p256dh, auth);
              sub.setId(existing.getId());
              sub.setNotifyNewOrders(existing.isNotifyNewOrders());
              sub.setNotifyCallWaiter(existing.isNotifyCallWaiter());
              repository.save(sub);
            },
            () ->
                repository.save(
                    PushSubscription.forStaff(restaurantId, staffId, endpoint, p256dh, auth)));
  }
}

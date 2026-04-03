package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import java.util.UUID;

public class Subscribe {

  private final PushSubscriptionRepository repository;

  public Subscribe(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  public void execute(UUID restaurantId, String endpoint, String p256dh, String auth) {
    repository
        .findByEndpoint(endpoint)
        .ifPresentOrElse(
            existing ->
                repository.save(
                    new PushSubscription(
                        existing.getId(),
                        restaurantId,
                        endpoint,
                        p256dh,
                        auth,
                        existing.isNotifyNewOrders(),
                        existing.isNotifyCallWaiter())),
            () -> repository.save(new PushSubscription(restaurantId, endpoint, p256dh, auth)));
  }
}

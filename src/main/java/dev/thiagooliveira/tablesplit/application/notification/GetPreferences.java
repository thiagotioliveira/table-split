package dev.thiagooliveira.tablesplit.application.notification;

import java.util.Map;
import java.util.Optional;

public class GetPreferences {

  private final PushSubscriptionRepository repository;

  public GetPreferences(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  public Optional<Map<String, Boolean>> execute(String endpoint) {
    return repository
        .findByEndpoint(endpoint)
        .map(
            sub ->
                Map.of(
                    "notifyNewOrders", sub.isNotifyNewOrders(),
                    "notifyCallWaiter", sub.isNotifyCallWaiter()));
  }
}

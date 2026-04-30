package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;

public class Unsubscribe {

  private final PushSubscriptionRepository repository;

  public Unsubscribe(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  public void execute(String endpoint) {
    repository.deleteByEndpoint(endpoint);
  }
}

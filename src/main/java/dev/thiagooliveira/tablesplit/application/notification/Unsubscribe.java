package dev.thiagooliveira.tablesplit.application.notification;

public class Unsubscribe {

  private final PushSubscriptionRepository repository;

  public Unsubscribe(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  public void execute(String endpoint) {
    repository.deleteByEndpoint(endpoint);
  }
}

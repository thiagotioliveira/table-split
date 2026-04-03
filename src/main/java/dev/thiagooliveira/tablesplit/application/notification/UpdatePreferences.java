package dev.thiagooliveira.tablesplit.application.notification;

public class UpdatePreferences {

  private final PushSubscriptionRepository repository;

  public UpdatePreferences(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  public void execute(String endpoint, boolean notifyNewOrders, boolean notifyCallWaiter) {
    repository
        .findByEndpoint(endpoint)
        .ifPresent(
            sub -> {
              sub.setNotifyNewOrders(notifyNewOrders);
              sub.setNotifyCallWaiter(notifyCallWaiter);
              repository.save(sub);
            });
  }
}

package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class Broadcaster {

  private final PushSubscriptionRepository repository;
  private final PushSender sender;

  public Broadcaster(PushSubscriptionRepository repository, PushSender sender) {
    this.repository = repository;
    this.sender = sender;
  }

  public void newOrder(UUID restaurantId, String payload) {
    broadcast(restaurantId, payload, PushSubscription::isNotifyNewOrders);
  }

  public void callWaiter(UUID restaurantId, String payload) {
    broadcast(restaurantId, payload, PushSubscription::isNotifyCallWaiter);
  }

  public void general(UUID restaurantId, String payload) {
    broadcast(restaurantId, payload, sub -> true);
  }

  private void broadcast(UUID restaurantId, String payload, Predicate<PushSubscription> filter) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    subscriptions.stream().filter(filter).forEach(sub -> sender.send(sub, payload));
  }
}

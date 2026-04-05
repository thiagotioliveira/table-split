package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Broadcaster {

  private static final Logger logger = LoggerFactory.getLogger(Broadcaster.class);

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
    long count = subscriptions.stream().filter(filter).count();
    logger.debug(
        "Broadcasting to {} subscriptions (total entries found: {})", count, subscriptions.size());
    subscriptions.stream().filter(filter).forEach(sub -> sender.send(sub, payload));
  }
}

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
    newOrder(restaurantId, payload, null);
  }

  public void newOrder(UUID restaurantId, String payload, UUID excludeId) {
    broadcast(restaurantId, payload, excludeId, PushSubscription::isNotifyNewOrders);
  }

  public void callWaiter(UUID restaurantId, String payload) {
    broadcast(restaurantId, payload, null, PushSubscription::isNotifyCallWaiter);
  }

  public void general(UUID restaurantId, String payload) {
    general(restaurantId, payload, null);
  }

  public void general(UUID restaurantId, String payload, UUID excludeId) {
    broadcast(restaurantId, payload, excludeId, sub -> true);
  }

  private void broadcast(
      UUID restaurantId, String payload, UUID excludeId, Predicate<PushSubscription> filter) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    Predicate<PushSubscription> finalFilter =
        sub -> {
          if (excludeId != null) {
            if (excludeId.equals(sub.getStaffId()) || excludeId.equals(sub.getUserId())) {
              return false;
            }
          }
          return filter.test(sub);
        };
    long count = subscriptions.stream().filter(finalFilter).count();
    logger.debug(
        "Broadcasting to {} subscriptions (total entries found: {})", count, subscriptions.size());
    subscriptions.stream().filter(finalFilter).forEach(sub -> sender.send(sub, payload));
  }
}

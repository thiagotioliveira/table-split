package dev.thiagooliveira.tablesplit.infrastructure.listener.notification;

import dev.thiagooliveira.tablesplit.application.notification.PushSubscriptionRepository;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.event.StaffUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationListener {

  private final PushSubscriptionRepository pushSubscriptionRepository;

  public PushNotificationListener(PushSubscriptionRepository pushSubscriptionRepository) {
    this.pushSubscriptionRepository = pushSubscriptionRepository;
  }

  @EventListener
  public void onStaffUpdated(StaffUpdatedEvent event) {
    var removed = event.removedModules();
    if (removed == null || removed.isEmpty()) {
      return;
    }

    boolean removeOrders = removed.contains(Module.ORDERS);
    boolean removeTables = removed.contains(Module.TABLES);

    if (removeOrders || removeTables) {
      var subscriptions =
          pushSubscriptionRepository.findAllByStaffIdAndRestaurantId(
              event.staffId(), event.restaurantId());
      for (var sub : subscriptions) {
        boolean changed = false;
        if (removeOrders && sub.isNotifyNewOrders()) {
          sub.setNotifyNewOrders(false);
          changed = true;
        }
        if (removeTables && sub.isNotifyCallWaiter()) {
          sub.setNotifyCallWaiter(false);
          changed = true;
        }
        if (changed) {
          pushSubscriptionRepository.save(sub);
        }
      }
    }
  }
}

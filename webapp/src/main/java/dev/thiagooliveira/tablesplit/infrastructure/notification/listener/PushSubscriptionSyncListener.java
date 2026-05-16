package dev.thiagooliveira.tablesplit.infrastructure.notification.listener;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.account.event.StaffUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.account.event.UserUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PushSubscriptionSyncListener {

  private final PushSubscriptionRepository pushSubscriptionRepository;

  public PushSubscriptionSyncListener(PushSubscriptionRepository pushSubscriptionRepository) {
    this.pushSubscriptionRepository = pushSubscriptionRepository;
  }

  @EventListener
  @Transactional
  public void onStaffUpdated(StaffUpdatedEvent event) {
    var removed = event.getRemovedModules();
    if (removed == null || removed.isEmpty()) {
      return;
    }

    boolean removeOrders = removed.contains(Module.ORDERS);
    boolean removeTables = removed.contains(Module.TABLES);

    if (removeOrders || removeTables) {
      var subscriptions =
          pushSubscriptionRepository.findAllByStaffIdAndRestaurantId(
              event.getStaffId(), event.getRestaurantId());
      for (var sub : subscriptions) {
        boolean changed = false;
        if (removeOrders) {
          if (sub.isNotifyNewOrders()) {
            sub.setNotifyNewOrders(false);
            changed = true;
          }
          if (sub.isNotifyOrderClosed()) {
            sub.setNotifyOrderClosed(false);
            changed = true;
          }
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

  @EventListener
  @Transactional
  public void onUserUpdated(UserUpdatedEvent event) {
    // Sync language for both userId and staffId linked subscriptions
    // Since we don't know if the user is a staff or admin here, we try both
    pushSubscriptionRepository.updateLanguageByUserId(event.getUserId(), event.getLanguage());
    pushSubscriptionRepository.updateLanguageByStaffId(event.getUserId(), event.getLanguage());
  }
}

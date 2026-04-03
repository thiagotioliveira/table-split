package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;

public interface PushSender {

  void send(PushSubscription subscription, String payload);
}

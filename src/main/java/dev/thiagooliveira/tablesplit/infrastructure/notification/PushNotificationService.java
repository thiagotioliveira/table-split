package dev.thiagooliveira.tablesplit.infrastructure.notification;

import dev.thiagooliveira.tablesplit.application.notification.PushSubscriptionRepository;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model.SubscriptionData;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PushNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

  @Value("${app.push.public-key}")
  private String publicKey;

  @Value("${app.push.private-key}")
  private String privateKey;

  @Value("${app.push.subject}")
  private String subject;

  private final PushSubscriptionRepository repository;
  private PushService pushService;

  public PushNotificationService(PushSubscriptionRepository repository) {
    this.repository = repository;
  }

  @PostConstruct
  public void init() throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    if (publicKey != null && !publicKey.isBlank() && privateKey != null && !privateKey.isBlank()) {
      this.pushService = new PushService(publicKey, privateKey, subject);
    } else {
      logger.warn("VAPID keys not configured. Push notifications will be disabled.");
    }
  }

  @Transactional
  public void subscribe(UUID restaurantId, SubscriptionData data) {
    logger.info(
        "Attempting push subscription for restaurant: {} at endpoint: {}",
        restaurantId,
        data.endpoint());
    repository
        .findByEndpoint(data.endpoint())
        .ifPresentOrElse(
            sub -> {
              logger.info(
                  "Updating existing push subscription ID: {} for restaurant: {}",
                  sub.getId(),
                  restaurantId);
              repository.save(
                  new PushSubscription(
                      sub.getId(),
                      restaurantId,
                      data.endpoint(),
                      data.p256dh(),
                      data.auth(),
                      sub.isNotifyNewOrders(),
                      sub.isNotifyCallWaiter()));
            },
            () -> {
              logger.info("Creating NEW push subscription for restaurant: {}", restaurantId);
              repository.save(
                  new PushSubscription(restaurantId, data.endpoint(), data.p256dh(), data.auth()));
            });
  }

  @Transactional
  public void unsubscribe(String endpoint) {
    logger.info("Unsubscribing endpoint: {}", endpoint);
    repository.deleteByEndpoint(endpoint);
  }

  @Transactional
  public void updatePreferences(
      String endpoint, boolean notifyNewOrders, boolean notifyCallWaiter) {
    logger.info("Updating preferences for endpoint: {}", endpoint);
    repository
        .findByEndpoint(endpoint)
        .ifPresent(
            sub -> {
              sub.setNotifyNewOrders(notifyNewOrders);
              sub.setNotifyCallWaiter(notifyCallWaiter);
              repository.save(sub);
            });
  }

  public java.util.Optional<java.util.Map<String, Boolean>> getPreferences(String endpoint) {
    return repository
        .findByEndpoint(endpoint)
        .map(
            sub ->
                java.util.Map.of(
                    "notifyNewOrders", sub.isNotifyNewOrders(),
                    "notifyCallWaiter", sub.isNotifyCallWaiter()));
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void sendNewOrderNotification(UUID restaurantId, String payload) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    List<PushSubscription> filtered =
        subscriptions.stream().filter(PushSubscription::isNotifyNewOrders).toList();

    broadcast(restaurantId, payload, filtered, "New Order");
  }

  public void sendCallWaiterNotification(UUID restaurantId, String payload) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    List<PushSubscription> filtered =
        subscriptions.stream().filter(PushSubscription::isNotifyCallWaiter).toList();

    broadcast(restaurantId, payload, filtered, "Call Waiter");
  }

  public void sendNotification(UUID restaurantId, String payload) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    broadcast(restaurantId, payload, subscriptions, "General");
  }

  private void broadcast(
      UUID restaurantId, String payload, List<PushSubscription> targetSubscriptions, String topic) {
    if (pushService == null) {
      logger.warn(
          "Cannot broadcast push notification: PushService is not initialized (VAPID keys missing)");
      return;
    }

    logger.info(
        "Broadcasting [{}] notification to restaurant: {}. Devices targeted: {}",
        topic,
        restaurantId,
        targetSubscriptions.size());

    for (PushSubscription sub : targetSubscriptions) {
      try {
        Subscription subscription =
            new Subscription(
                sub.getEndpoint(), new Subscription.Keys(sub.getP256dh(), sub.getAuth()));

        Notification notification = new Notification(subscription, payload);
        pushService.send(notification);
      } catch (GeneralSecurityException
          | IOException
          | JoseException
          | ExecutionException
          | InterruptedException e) {
        logger.error(
            "Failed to send push notification to {}: {}", sub.getEndpoint(), e.getMessage());
        if (e.getMessage() != null
            && (e.getMessage().contains("410") || e.getMessage().contains("404"))) {
          logger.warn("Subscription expired, removing: {}", sub.getEndpoint());
          repository.deleteByEndpoint(sub.getEndpoint());
        }
      }
    }
  }
}

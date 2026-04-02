package dev.thiagooliveira.tablesplit.infrastructure.notification;

import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
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
    this.pushService = new PushService(publicKey, privateKey, subject);
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
                      sub.getId(), restaurantId, data.endpoint(), data.p256dh(), data.auth()));
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

  public String getPublicKey() {
    return publicKey;
  }

  public void sendNotification(UUID restaurantId, String payload) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    logger.info(
        "Broadcasting notification to restaurant: {}. Total registered devices: {}",
        restaurantId,
        subscriptions.size());

    for (PushSubscription sub : subscriptions) {
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

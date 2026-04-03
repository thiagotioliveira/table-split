package dev.thiagooliveira.tablesplit.infrastructure.notification;

import dev.thiagooliveira.tablesplit.application.notification.PushSubscriptionRepository;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
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

@Service
public class PushNotificationService
    implements dev.thiagooliveira.tablesplit.application.notification.PushSender {

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

  @Override
  public void send(PushSubscription sub, String payload) {
    if (pushService == null) {
      logger.warn(
          "Cannot send push notification: PushService is not initialized (VAPID keys missing)");
      return;
    }

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
      logger.error("Failed to send push notification to {}: {}", sub.getEndpoint(), e.getMessage());
      if (e.getMessage() != null
          && (e.getMessage().contains("410") || e.getMessage().contains("404"))) {
        logger.warn("Subscription expired, removing: {}", sub.getEndpoint());
        repository.deleteByEndpoint(sub.getEndpoint());
      }
    }
  }

  public String getPublicKey() {
    return publicKey;
  }
}

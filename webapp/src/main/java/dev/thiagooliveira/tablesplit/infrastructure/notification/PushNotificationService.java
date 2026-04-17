package dev.thiagooliveira.tablesplit.infrastructure.notification;

import dev.thiagooliveira.tablesplit.application.notification.PushSubscriptionRepository;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import jakarta.annotation.PostConstruct;
import java.security.GeneralSecurityException;
import java.security.Security;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Urgency;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
    if (Security.getProvider("BC") == null) {
      Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    if (publicKey != null && !publicKey.isBlank() && privateKey != null && !privateKey.isBlank()) {
      logger.debug(
          "Initializing PushService with Public Key starting with: {}...",
          publicKey.substring(0, 10));
      logger.debug("Subject: {}", subject);
      this.pushService = new PushService(publicKey.trim(), privateKey.trim(), subject);
      logger.debug("PushService initialized successfully.");
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
      logger.debug("Preparing notification for endpoint: {}", sub.getEndpoint());

      Notification notification =
          Notification.builder()
              .endpoint(sub.getEndpoint())
              .userPublicKey(sub.getP256dh())
              .userAuth(sub.getAuth())
              .payload(payload)
              .urgency(Urgency.HIGH)
              .ttl(3600)
              .build();
      var response = pushService.send(notification, nl.martijndwars.webpush.Encoding.AES128GCM);
      int statusCode = response.getStatusLine().getStatusCode();

      logger.debug("Service provider response code: {}", statusCode);

      if (statusCode >= 200 && statusCode < 300) {
        logger.debug("Notification accepted by provider.");
      } else {
        logger.error(
            "REJECTED! Status: {} {}", statusCode, response.getStatusLine().getReasonPhrase());
        if (response.getEntity() != null) {
          String errorBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
          logger.error("Error body from provider: {}", errorBody);
        }
      }
    } catch (Exception e) {
      logger.error("CRITICAL ERROR in send()", e);
      if (e.getMessage() != null
          && (e.getMessage().contains("410") || e.getMessage().contains("404"))) {
        logger.warn("Subscription expired, removing: {}", sub.getEndpoint());
        repository.deleteByEndpoint(sub.getEndpoint());
      }
    }
  }

  public String getPublicKey() {
    return publicKey != null ? publicKey.trim() : null;
  }
}

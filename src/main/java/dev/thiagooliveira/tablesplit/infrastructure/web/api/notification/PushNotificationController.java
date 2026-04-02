package dev.thiagooliveira.tablesplit.infrastructure.web.api.notification;

import dev.thiagooliveira.tablesplit.infrastructure.notification.PushNotificationService;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model.SubscriptionData;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class PushNotificationController {

  private final PushNotificationService pushNotificationService;

  public PushNotificationController(PushNotificationService pushNotificationService) {
    this.pushNotificationService = pushNotificationService;
  }

  @PostMapping("/subscribe")
  public ResponseEntity<Void> subscribe(Authentication auth, @RequestBody SubscriptionData data) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();

    pushNotificationService.subscribe(restaurantId, data);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unsubscribe")
  public ResponseEntity<Void> unsubscribe(@RequestBody String endpoint) {
    pushNotificationService.unsubscribe(endpoint);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/public-key")
  public ResponseEntity<String> getPublicKey() {
    return ResponseEntity.ok(pushNotificationService.getPublicKey());
  }

  @PostMapping("/test")
  public ResponseEntity<Void> sendTest(Authentication auth) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();
    String payload =
        String.format(
            "{\"title\": \"Teste TableSplit\", \"body\": \"Push funcionando para o Restaurante: %s\", \"url\": \"/settings\"}",
            restaurantId);
    pushNotificationService.sendNotification(restaurantId, payload);
    return ResponseEntity.ok().build();
  }
}

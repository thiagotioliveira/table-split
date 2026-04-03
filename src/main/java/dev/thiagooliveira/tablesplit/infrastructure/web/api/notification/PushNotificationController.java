package dev.thiagooliveira.tablesplit.infrastructure.web.api.notification;

import dev.thiagooliveira.tablesplit.application.notification.*;
import dev.thiagooliveira.tablesplit.infrastructure.notification.PushNotificationService;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model.SubscriptionData;
import dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model.UpdatePreferencesRequest;
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
  private final Subscribe subscribe;
  private final Unsubscribe unsubscribe;
  private final GetPreferences getPreferences;
  private final UpdatePreferences updatePreferences;
  private final Broadcaster broadcaster;

  public PushNotificationController(
      PushNotificationService pushNotificationService,
      Subscribe subscribe,
      Unsubscribe unsubscribe,
      GetPreferences getPreferences,
      UpdatePreferences updatePreferences,
      Broadcaster broadcaster) {
    this.pushNotificationService = pushNotificationService;
    this.subscribe = subscribe;
    this.unsubscribe = unsubscribe;
    this.getPreferences = getPreferences;
    this.updatePreferences = updatePreferences;
    this.broadcaster = broadcaster;
  }

  @PostMapping("/subscribe")
  public ResponseEntity<Void> subscribe(Authentication auth, @RequestBody SubscriptionData data) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();

    subscribe.execute(restaurantId, data.endpoint(), data.p256dh(), data.auth());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unsubscribe")
  public ResponseEntity<Void> unsubscribe(@RequestBody String endpoint) {
    unsubscribe.execute(endpoint);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/public-key")
  public ResponseEntity<String> getPublicKey() {
    return ResponseEntity.ok(pushNotificationService.getPublicKey());
  }

  @PostMapping("/status")
  public ResponseEntity<java.util.Map<String, Boolean>> getStatus(@RequestBody String endpoint) {
    return getPreferences
        .execute(endpoint)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/preferences")
  public ResponseEntity<Void> updatePreferences(@RequestBody UpdatePreferencesRequest request) {
    updatePreferences.execute(
        request.endpoint(), request.notifyNewOrders(), request.notifyCallWaiter());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/test")
  public ResponseEntity<Void> sendTest(Authentication auth) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();
    String payload =
        String.format(
            "{\"title\": \"Teste TableSplit\", \"body\": \"Push funcionando para o Restaurante: %s\", \"url\": \"/profile\"}",
            restaurantId);
    broadcaster.general(restaurantId, payload);
    return ResponseEntity.ok().build();
  }
}

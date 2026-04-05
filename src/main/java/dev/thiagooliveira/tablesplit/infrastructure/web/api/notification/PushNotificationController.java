package dev.thiagooliveira.tablesplit.infrastructure.web.api.notification;

import dev.thiagooliveira.tablesplit.application.notification.*;
import dev.thiagooliveira.tablesplit.infrastructure.notification.PushNotificationService;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model.SubscriptionData;
import dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model.UpdatePreferencesRequest;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(PushNotificationController.class);

  private final PushNotificationService pushNotificationService;
  private final Subscribe subscribe;
  private final Unsubscribe unsubscribe;
  private final GetPreferences getPreferences;
  private final UpdatePreferences updatePreferences;
  private final Broadcaster broadcaster;
  private final ListActiveWaiterCalls listActiveWaiterCalls;
  private final DismissWaiterCall dismissWaiterCall;

  public PushNotificationController(
      PushNotificationService pushNotificationService,
      Subscribe subscribe,
      Unsubscribe unsubscribe,
      GetPreferences getPreferences,
      UpdatePreferences updatePreferences,
      Broadcaster broadcaster,
      ListActiveWaiterCalls listActiveWaiterCalls,
      DismissWaiterCall dismissWaiterCall) {
    this.pushNotificationService = pushNotificationService;
    this.subscribe = subscribe;
    this.unsubscribe = unsubscribe;
    this.getPreferences = getPreferences;
    this.updatePreferences = updatePreferences;
    this.broadcaster = broadcaster;
    this.listActiveWaiterCalls = listActiveWaiterCalls;
    this.dismissWaiterCall = dismissWaiterCall;
  }

  @PostMapping("/subscribe")
  public ResponseEntity<Void> subscribe(Authentication auth, @RequestBody SubscriptionData data) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();

    UUID id = context.getUser().getId();
    if (context.getUser().getRole()
        == dev.thiagooliveira.tablesplit.domain.account.Role.RESTAURANT_ADMIN) {
      subscribe.executeForUser(restaurantId, id, data.endpoint(), data.p256dh(), data.auth());
    } else {
      subscribe.executeForStaff(restaurantId, id, data.endpoint(), data.p256dh(), data.auth());
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unsubscribe")
  public ResponseEntity<Void> unsubscribe(@RequestBody String endpoint) {
    unsubscribe.execute(endpoint);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/public-key")
  public ResponseEntity<String> getPublicKey() {
    String key = pushNotificationService.getPublicKey();
    logger.debug(
        "Serving Public Key to browser: {}", (key != null ? key.substring(0, 10) + "..." : "NULL"));
    return ResponseEntity.ok()
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .body(key);
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
    logger.debug("Received test notification request for restaurant: {}", restaurantId);
    String payload =
        String.format(
            "{\"title\": \"Teste TableSplit\", \"body\": \"Push funcionando para o Restaurante: %s\", \"url\": \"/profile\"}",
            restaurantId);
    broadcaster.general(restaurantId, payload);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/active-calls")
  public ResponseEntity<
          java.util.List<
              dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model
                  .WaiterCallResponse>>
      getActiveCalls(Authentication auth) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();

    java.util.List<
            dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model
                .WaiterCallResponse>
        response =
            listActiveWaiterCalls.execute(restaurantId).stream()
                .map(
                    call ->
                        new dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model
                            .WaiterCallResponse(
                            call.getId(),
                            call.getTableCod(),
                            call.getCreatedAt(),
                            call.getCallCount()))
                .toList();

    return ResponseEntity.ok(response);
  }

  @PostMapping("/calls/dismiss")
  public ResponseEntity<Void> dismissCall(@RequestBody UUID id) {
    dismissWaiterCall.execute(id);
    return ResponseEntity.ok().build();
  }
}

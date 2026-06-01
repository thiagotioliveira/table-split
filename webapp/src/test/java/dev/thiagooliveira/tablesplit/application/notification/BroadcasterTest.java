package dev.thiagooliveira.tablesplit.application.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;

class BroadcasterTest {

  private PushSubscriptionRepository repository;
  private PushSender sender;
  private MessageSource messageSource;
  private Broadcaster broadcaster;

  @BeforeEach
  void setUp() {
    repository = mock(PushSubscriptionRepository.class);
    sender = mock(PushSender.class);
    messageSource = mock(MessageSource.class);
    broadcaster = new Broadcaster(repository, sender, messageSource);
  }

  @Test
  void shouldBroadcastNewOrderAlertSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T10";
    String customerName = "John Doe";
    UUID excludeId = UUID.randomUUID();

    PushSubscription sub = new PushSubscription();
    sub.setRestaurantId(restaurantId);
    sub.setNotifyNewOrders(true);
    sub.setLanguage(Language.PT);
    sub.setUserId(UUID.randomUUID());

    when(repository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(sub));
    when(messageSource.getMessage(eq("notification.push.new_order.title"), any(), any()))
        .thenReturn("Novo Pedido");
    when(messageSource.getMessage(eq("notification.push.new_order.body"), any(), any()))
        .thenReturn("Mesa T10 - John Doe");

    broadcaster.newOrder(restaurantId, tableCod, customerName, excludeId);

    ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(sender).send(eq(sub), payloadCaptor.capture());

    String payload = payloadCaptor.getValue();
    assertTrue(payload.contains("Novo Pedido"));
    assertTrue(payload.contains("Mesa T10 - John Doe"));
  }

  @Test
  void shouldBroadcastCallWaiterAlertSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T12";

    PushSubscription sub = new PushSubscription();
    sub.setRestaurantId(restaurantId);
    sub.setNotifyCallWaiter(true);
    sub.setLanguage(Language.EN);
    sub.setUserId(UUID.randomUUID());

    when(repository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(sub));
    when(messageSource.getMessage(eq("notification.push.waiter_call.title"), any(), any()))
        .thenReturn("Waiter Called");
    when(messageSource.getMessage(eq("notification.push.waiter_call.body"), any(), any()))
        .thenReturn("Table T12 needs waiter");

    broadcaster.callWaiter(restaurantId, tableCod);

    ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(sender).send(eq(sub), payloadCaptor.capture());

    String payload = payloadCaptor.getValue();
    assertTrue(payload.contains("Waiter Called"));
    assertTrue(payload.contains("Table T12 needs waiter"));
  }

  @Test
  void shouldBroadcastOrderClosedForTableSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T05";
    BigDecimal amount = BigDecimal.valueOf(120.00);
    UUID excludeId = UUID.randomUUID();

    PushSubscription sub = new PushSubscription();
    sub.setRestaurantId(restaurantId);
    sub.setNotifyOrderClosed(true);
    sub.setLanguage(Language.PT);
    sub.setUserId(UUID.randomUUID());

    when(repository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(sub));
    when(messageSource.getMessage(eq("notification.push.order_closed.table.title"), any(), any()))
        .thenReturn("Mesa Fechada");
    when(messageSource.getMessage(eq("notification.push.order_closed.table.body"), any(), any()))
        .thenReturn("A mesa T05 foi fechada.");

    broadcaster.orderClosed(restaurantId, tableCod, amount, Currency.EUR, excludeId);

    ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(sender).send(eq(sub), payloadCaptor.capture());

    String payload = payloadCaptor.getValue();
    assertTrue(payload.contains("Mesa Fechada"));
    assertTrue(payload.contains("A mesa T05 foi fechada."));
  }

  @Test
  void shouldBroadcastGeneralNotificationSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String titleKey = "custom.title";
    String url = "/custom-url";

    PushSubscription sub = new PushSubscription();
    sub.setRestaurantId(restaurantId);
    sub.setLanguage(Language.PT);
    sub.setUserId(UUID.randomUUID());

    when(repository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(sub));
    when(messageSource.getMessage(eq(titleKey), any(), any()))
        .thenReturn("Custom Notification Title");

    broadcaster.general(restaurantId, titleKey, null, url);

    ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(sender).send(eq(sub), payloadCaptor.capture());

    String payload = payloadCaptor.getValue();
    assertTrue(payload.contains("Custom Notification Title"));
    assertTrue(payload.contains(url));
  }
}

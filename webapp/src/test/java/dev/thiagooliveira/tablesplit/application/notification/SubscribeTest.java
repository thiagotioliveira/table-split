package dev.thiagooliveira.tablesplit.application.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SubscribeTest {

  private PushSubscriptionRepository repository;
  private Subscribe subscribe;

  @BeforeEach
  void setUp() {
    repository = mock(PushSubscriptionRepository.class);
    subscribe = new Subscribe(repository);
  }

  @Test
  void shouldSubscribeNewUserSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    String endpoint = "https://fcm.googleapis.com/fcm/send/endpoint123";
    String p256dh = "p256dhkey";
    String auth = "authkey";
    Language language = Language.PT;

    when(repository.findByEndpoint(endpoint)).thenReturn(Optional.empty());

    subscribe.executeForUser(restaurantId, userId, endpoint, p256dh, auth, language);

    ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
    verify(repository).save(captor.capture());

    PushSubscription saved = captor.getValue();
    assertNotNull(saved);
    assertEquals(restaurantId, saved.getRestaurantId());
    assertEquals(userId, saved.getUserId());
    assertNull(saved.getStaffId());
    assertEquals(endpoint, saved.getEndpoint());
    assertEquals(p256dh, saved.getP256dh());
    assertEquals(auth, saved.getAuth());
    assertEquals(language, saved.getLanguage());
  }

  @Test
  void shouldUpdateExistingUserSubscriptionSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    String endpoint = "https://fcm.googleapis.com/fcm/send/endpoint123";
    String p256dh = "p256dhkey";
    String auth = "authkey";
    Language language = Language.PT;

    PushSubscription existing =
        PushSubscription.forUser(restaurantId, userId, endpoint, p256dh, auth, language);
    existing.setId(UUID.randomUUID());
    existing.setNotifyNewOrders(false);

    when(repository.findByEndpoint(endpoint)).thenReturn(Optional.of(existing));

    subscribe.executeForUser(restaurantId, userId, endpoint, p256dh, auth, language);

    ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
    verify(repository).save(captor.capture());

    PushSubscription saved = captor.getValue();
    assertNotNull(saved);
    assertEquals(existing.getId(), saved.getId());
    assertFalse(saved.isNotifyNewOrders()); // preserved property
  }

  @Test
  void shouldSubscribeNewStaffSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID staffId = UUID.randomUUID();
    String endpoint = "https://fcm.googleapis.com/fcm/send/endpoint456";
    String p256dh = "p256dhkey";
    String auth = "authkey";
    Language language = Language.EN;

    when(repository.findByEndpoint(endpoint)).thenReturn(Optional.empty());

    subscribe.executeForStaff(restaurantId, staffId, endpoint, p256dh, auth, language);

    ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
    verify(repository).save(captor.capture());

    PushSubscription saved = captor.getValue();
    assertNotNull(saved);
    assertEquals(restaurantId, saved.getRestaurantId());
    assertEquals(staffId, saved.getStaffId());
    assertNull(saved.getUserId());
    assertEquals(endpoint, saved.getEndpoint());
    assertEquals(language, saved.getLanguage());
  }

  @Test
  void shouldUpdateExistingStaffSubscriptionSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID staffId = UUID.randomUUID();
    String endpoint = "https://fcm.googleapis.com/fcm/send/endpoint456";
    String p256dh = "p256dhkey";
    String auth = "authkey";
    Language language = Language.EN;

    PushSubscription existing =
        PushSubscription.forStaff(restaurantId, staffId, endpoint, p256dh, auth, language);
    existing.setId(UUID.randomUUID());
    existing.setNotifyCallWaiter(false);

    when(repository.findByEndpoint(endpoint)).thenReturn(Optional.of(existing));

    subscribe.executeForStaff(restaurantId, staffId, endpoint, p256dh, auth, language);

    ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
    verify(repository).save(captor.capture());

    PushSubscription saved = captor.getValue();
    assertNotNull(saved);
    assertEquals(existing.getId(), saved.getId());
    assertFalse(saved.isNotifyCallWaiter()); // preserved property
  }
}

package dev.thiagooliveira.tablesplit.application.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCallRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RegisterWaiterCallTest {

  private WaiterCallRepository repository;
  private Broadcaster broadcaster;
  private RegisterWaiterCall registerWaiterCall;

  @BeforeEach
  void setUp() {
    repository = mock(WaiterCallRepository.class);
    broadcaster = mock(Broadcaster.class);
    registerWaiterCall = new RegisterWaiterCall(repository, broadcaster);
  }

  @Test
  void shouldRegisterNewWaiterCallSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T12";

    when(repository.findActiveByRestaurantIdAndTableCod(restaurantId, tableCod))
        .thenReturn(Optional.empty());

    WaiterCall callMock =
        new WaiterCall(
            UUID.randomUUID(),
            restaurantId,
            tableCod,
            java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC));
    when(repository.findAllActiveByRestaurantId(restaurantId)).thenReturn(List.of(callMock));

    WaiterCall result = registerWaiterCall.execute(restaurantId, tableCod);

    assertNotNull(result);
    assertEquals(restaurantId, result.getRestaurantId());
    assertEquals(tableCod, result.getTableCod());
    assertEquals(1, result.getCallCount());

    verify(repository).save(result);
    verify(broadcaster).callWaiter(eq(restaurantId), anyString());
  }

  @Test
  void shouldIncrementAndRegisterExistingWaiterCallSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T12";
    WaiterCall existingCall =
        new WaiterCall(
            UUID.randomUUID(),
            restaurantId,
            tableCod,
            java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC));
    existingCall.incrementCount(); // count becomes 2

    when(repository.findActiveByRestaurantIdAndTableCod(restaurantId, tableCod))
        .thenReturn(Optional.of(existingCall));
    when(repository.findAllActiveByRestaurantId(restaurantId)).thenReturn(List.of(existingCall));

    WaiterCall result = registerWaiterCall.execute(restaurantId, tableCod);

    assertNotNull(result);
    assertEquals(existingCall.getId(), result.getId());
    assertEquals(3, result.getCallCount()); // increments again in execute to 3

    verify(repository).save(existingCall);

    ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(broadcaster).callWaiter(eq(restaurantId), payloadCaptor.capture());

    String payload = payloadCaptor.getValue();
    assertTrue(payload.contains("\"count\": 3"));
    assertTrue(payload.contains("\"totalCount\": 1"));
  }
}

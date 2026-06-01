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

class DismissWaiterCallTest {

  private WaiterCallRepository repository;
  private DismissWaiterCall dismissWaiterCall;

  @BeforeEach
  void setUp() {
    repository = mock(WaiterCallRepository.class);
    dismissWaiterCall = new DismissWaiterCall(repository);
  }

  @Test
  void shouldDismissWaiterCallSuccessfully() {
    UUID id = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T10";

    WaiterCall call =
        new WaiterCall(
            id, restaurantId, tableCod, java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC));
    assertFalse(call.isDismissed());

    when(repository.findById(id)).thenReturn(Optional.of(call));
    when(repository.findAllActiveByRestaurantId(restaurantId)).thenReturn(List.of(call));

    dismissWaiterCall.execute(id);

    assertTrue(call.isDismissed());
    assertNotNull(call.getDismissedAt());
    verify(repository).save(call);
  }

  @Test
  void shouldDoNothingWhenWaiterCallDoesNotExist() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());

    dismissWaiterCall.execute(id);

    verify(repository, never()).save(any());
  }
}

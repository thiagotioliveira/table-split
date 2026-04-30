package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncTableStatusTest {

  @Mock private TableRepository tableRepository;

  private SyncTableStatus syncTableStatus;

  @BeforeEach
  void setUp() {
    syncTableStatus = new SyncTableStatus(tableRepository);
  }

  @Test
  void shouldSetTableToAvailableWhenOrderIsClosed() {
    // Given
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, restaurantId, "1");
    table.occupy();

    Order order = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    order.setStatus(OrderStatus.CLOSED);

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    // When
    syncTableStatus.execute(order);

    // Then
    verify(tableRepository).save(table);
    assertEquals(TableStatus.AVAILABLE, table.getStatus());
  }

  @Test
  void shouldSetTableToWaitingWhenOrderIsWaiting() {
    // Given
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, restaurantId, "1");
    table.setStatus(TableStatus.AVAILABLE);

    Order order = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    order.setStatus(OrderStatus.WAITING);

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    // When
    syncTableStatus.execute(order);

    // Then
    verify(tableRepository).save(table);
    assertEquals(TableStatus.WAITING, table.getStatus());
  }

  @Test
  void shouldSetTableToOccupiedWhenOrderIsOpen() {
    // Given
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, restaurantId, "1");
    table.setStatus(TableStatus.AVAILABLE);

    Order order = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    order.setStatus(OrderStatus.OPEN);

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    // When
    syncTableStatus.execute(order);

    // Then
    verify(tableRepository).save(table);
    assertEquals(TableStatus.OCCUPIED, table.getStatus());
  }
}

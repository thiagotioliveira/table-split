package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenTableTest {

  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private SyncTableStatus syncTableStatus;
  private OpenTable openTable;

  @BeforeEach
  void setUp() {
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    syncTableStatus = mock(SyncTableStatus.class);
    openTable = new OpenTable(tableRepository, orderRepository, syncTableStatus);
  }

  @Test
  void shouldOpenTableSuccessfully_whenTableIsAvailable() {
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, restaurantId, "01");

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order result = openTable.execute(tableId, 10, null, null);

    assertNotNull(result);
    assertEquals(restaurantId, result.getRestaurantId());
    assertEquals(tableId, result.getTableId());
    assertEquals(OrderStatus.OPEN, result.getStatus());
    assertEquals(TableStatus.OCCUPIED, table.getStatus());

    verify(tableRepository, atLeastOnce()).save(table);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void shouldReturnExistingOrder_whenTableIsOccupied() {
    UUID tableId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Table table = new Table(tableId, restaurantId, "01");
    table.occupy();
    Order existingOrder = new Order(UUID.randomUUID(), restaurantId, tableId, 10);

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(existingOrder));

    Order result = openTable.execute(tableId, 10, null, null);

    assertEquals(existingOrder, result);
    verify(orderRepository, never()).save(any(Order.class));
  }
}

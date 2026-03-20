package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.domain.event.TableOpenedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OpenTableTest {

  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private EventPublisher eventPublisher;
  private OpenTable openTable;

  @BeforeEach
  void setUp() {
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    eventPublisher = mock(EventPublisher.class);
    openTable = new OpenTable(tableRepository, orderRepository, eventPublisher);
  }

  @Test
  void shouldOpenTableSuccessfully_whenTableIsAvailable() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T1";
    Table table = new Table(UUID.randomUUID(), restaurantId, tableCod);

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.of(table));

    Order result = openTable.execute(restaurantId, tableCod);

    assertNotNull(result);
    assertEquals(restaurantId, result.getRestaurantId());
    assertEquals(table.getId(), result.getTableId());
    assertEquals(OrderStatus.OPEN, result.getStatus());
    assertEquals(TableStatus.OCCUPIED, table.getStatus());

    verify(tableRepository, atLeastOnce()).save(table);
    verify(orderRepository).save(any(Order.class));
    verify(eventPublisher).publishEvent(any(TableOpenedEvent.class));
  }

  @Test
  void shouldCreateTableAndOpenOrder_whenTableDoesNotExist() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T-NEW";

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.empty());

    Order result = openTable.execute(restaurantId, tableCod);

    assertNotNull(result);

    ArgumentCaptor<Table> tableCaptor = ArgumentCaptor.forClass(Table.class);
    verify(tableRepository, atLeastOnce()).save(tableCaptor.capture());
    Table createdTable = tableCaptor.getValue();

    assertEquals(tableCod, createdTable.getCod());
    assertEquals(TableStatus.OCCUPIED, createdTable.getStatus());
    assertEquals(createdTable.getId(), result.getTableId());
  }

  @Test
  void shouldThrowTableAlreadyOccupied_whenTableIsOccupied() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T1";
    Table table = new Table(UUID.randomUUID(), restaurantId, tableCod);
    table.occupy();

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.of(table));

    assertThrows(TableAlreadyOccupied.class, () -> openTable.execute(restaurantId, tableCod));

    verify(orderRepository, never()).save(any());
  }
}

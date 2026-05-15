package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferTableTest {

  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private TransferTable transferTable;

  @BeforeEach
  void setUp() {
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    transferTable = new TransferTable(tableRepository, orderRepository);
  }

  @Test
  void shouldTransferOrderSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID sourceTableId = UUID.randomUUID();
    UUID targetTableId = UUID.randomUUID();

    Table sourceTable = new Table(sourceTableId, restaurantId, "01");
    sourceTable.setStatus(TableStatus.OCCUPIED);

    Table targetTable = new Table(targetTableId, restaurantId, "02");
    targetTable.setStatus(TableStatus.AVAILABLE);

    Order order = new Order(UUID.randomUUID(), restaurantId, sourceTableId, 10);

    when(tableRepository.findById(sourceTableId)).thenReturn(Optional.of(sourceTable));
    when(tableRepository.findById(targetTableId)).thenReturn(Optional.of(targetTable));
    when(orderRepository.findActiveOrderByTableId(sourceTableId)).thenReturn(Optional.of(order));

    transferTable.execute(sourceTableId, targetTableId);

    assertEquals(targetTableId, order.getTableId());
    assertEquals(TableStatus.AVAILABLE, sourceTable.getStatus());
    assertEquals(TableStatus.OCCUPIED, targetTable.getStatus());

    verify(tableRepository).save(sourceTable);
    verify(tableRepository).save(targetTable);
    verify(orderRepository).save(order);
  }

  @Test
  void shouldThrowException_whenSourceTableNotFound() {
    UUID sourceTableId = UUID.randomUUID();
    UUID targetTableId = UUID.randomUUID();

    when(tableRepository.findById(sourceTableId)).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> transferTable.execute(sourceTableId, targetTableId));

    assertEquals("Source table not found", exception.getMessage());
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldThrowException_whenTargetTableNotFound() {
    UUID sourceTableId = UUID.randomUUID();
    UUID targetTableId = UUID.randomUUID();
    Table sourceTable = new Table(sourceTableId, UUID.randomUUID(), "01");

    when(tableRepository.findById(sourceTableId)).thenReturn(Optional.of(sourceTable));
    when(tableRepository.findById(targetTableId)).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> transferTable.execute(sourceTableId, targetTableId));

    assertEquals("Target table not found", exception.getMessage());
    verify(orderRepository, never()).findActiveOrderByTableId(any());
  }

  @Test
  void shouldThrowException_whenNoActiveOrderFound() {
    UUID sourceTableId = UUID.randomUUID();
    UUID targetTableId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Table sourceTable = new Table(sourceTableId, restaurantId, "01");
    Table targetTable = new Table(targetTableId, restaurantId, "02");

    when(tableRepository.findById(sourceTableId)).thenReturn(Optional.of(sourceTable));
    when(tableRepository.findById(targetTableId)).thenReturn(Optional.of(targetTable));
    when(orderRepository.findActiveOrderByTableId(sourceTableId)).thenReturn(Optional.empty());

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> transferTable.execute(sourceTableId, targetTableId));

    assertEquals("No active order found for source table", exception.getMessage());
  }

  @Test
  void shouldThrowException_whenTargetTableNotAvailable() {
    UUID restaurantId = UUID.randomUUID();
    UUID sourceTableId = UUID.randomUUID();
    UUID targetTableId = UUID.randomUUID();

    Table sourceTable = new Table(sourceTableId, restaurantId, "01");
    sourceTable.occupy();

    Table targetTable = new Table(targetTableId, restaurantId, "02");
    targetTable.occupy(); // Already occupied

    Order order = new Order(UUID.randomUUID(), restaurantId, sourceTableId, 10);

    when(tableRepository.findById(sourceTableId)).thenReturn(Optional.of(sourceTable));
    when(tableRepository.findById(targetTableId)).thenReturn(Optional.of(targetTable));
    when(orderRepository.findActiveOrderByTableId(sourceTableId)).thenReturn(Optional.of(order));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> transferTable.execute(sourceTableId, targetTableId));

    assertEquals("Target table is not available", exception.getMessage());
    verify(orderRepository, never()).save(any());
  }
}

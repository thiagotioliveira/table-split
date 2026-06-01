package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloseTableTest {

  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private CloseTable closeTable;

  @BeforeEach
  void setUp() {
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    closeTable = new CloseTable(tableRepository, orderRepository);
  }

  @Test
  void shouldCloseTableSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    Table table = new Table(tableId, restaurantId, "T1");
    table.occupy();

    Order order = new Order(orderId, restaurantId, tableId, 10);
    Ticket ticket = new Ticket();
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    item.setStatus(TicketStatus.PENDING);
    ticket.getItems().add(item);
    order.addTicket(ticket);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order result = closeTable.execute(orderId, Language.PT, UUID.randomUUID());

    assertEquals(OrderStatus.CLOSED, result.getStatus());
    assertEquals(TableStatus.AVAILABLE, table.getStatus());
    assertNotNull(result.getClosedAt());

    verify(orderRepository).save(order);
    verify(tableRepository).save(table);
  }

  @Test
  void shouldDeleteOrder_whenOrderIsInvalid() {
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    Table table = new Table(tableId, restaurantId, "T1");
    table.occupy();

    Order order = new Order(orderId, restaurantId, tableId, 10);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    closeTable.execute(orderId, Language.PT, UUID.randomUUID());

    assertEquals(TableStatus.AVAILABLE, table.getStatus());
    verify(orderRepository).delete(orderId);
    verify(tableRepository).save(table);
  }

  @Test
  void shouldBeIdempotent_whenOrderIsAlreadyClosed() {
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    Table table = new Table(tableId, restaurantId, "T1");
    Order order = new Order(orderId, restaurantId, tableId, 10);
    Ticket ticket = new Ticket();
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    item.setStatus(TicketStatus.PENDING);
    ticket.getItems().add(item);
    order.addTicket(ticket);
    order.close(Language.PT, UUID.randomUUID());

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order result = closeTable.execute(orderId, Language.PT, UUID.randomUUID());

    assertEquals(OrderStatus.CLOSED, result.getStatus());
    assertEquals(TableStatus.AVAILABLE, table.getStatus());
    verify(tableRepository).save(table);
  }

  @Test
  void shouldThrowExceptionWhenOrderOrTableNotFound() {
    UUID orderId = UUID.randomUUID();
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    var initiatedBy = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () -> closeTable.execute(orderId, Language.PT, initiatedBy));

    UUID orderId2 = UUID.randomUUID();
    UUID tableId2 = UUID.randomUUID();
    Order order = new Order(orderId2, UUID.randomUUID(), tableId2, 10);
    when(orderRepository.findById(orderId2)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId2)).thenReturn(Optional.empty());

    var initiatedBy2 = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () -> closeTable.execute(orderId2, Language.PT, initiatedBy2));
  }

  @Test
  void shouldCloseTakeawayOrderSuccessfully() {
    UUID orderId = UUID.randomUUID();
    Order order = Order.takeaway(UUID.randomUUID(), 10);
    order.setId(orderId);

    // Invalid takeaway (empty tickets)
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    closeTable.execute(orderId, Language.PT, UUID.randomUUID());
    verify(orderRepository).delete(orderId);

    // Valid takeaway
    Order order2 = Order.takeaway(UUID.randomUUID(), 10);
    order2.setId(orderId);
    Ticket ticket = new Ticket();
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    ticket.getItems().add(item);
    order2.addTicket(ticket);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order2));
    closeTable.execute(orderId, Language.PT, UUID.randomUUID());
    assertEquals(OrderStatus.CLOSED, order2.getStatus());
    verify(orderRepository).save(order2);
  }

  @Test
  void shouldCloseTableByCodeSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T01";
    UUID tableId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    Table table = new Table(tableId, restaurantId, tableCod);
    table.occupy();

    Order order = new Order(orderId, restaurantId, tableId, 10);
    Ticket ticket = new Ticket();
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    ticket.getItems().add(item);
    order.addTicket(ticket);

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(order));

    closeTable.execute(restaurantId, tableCod, Language.PT, UUID.randomUUID());

    assertEquals(OrderStatus.CLOSED, order.getStatus());
    assertEquals(TableStatus.AVAILABLE, table.getStatus());

    // Test invalid table by code (no tickets)
    Order order2 = new Order(orderId, restaurantId, tableId, 10);
    table.occupy();
    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(order2));

    closeTable.execute(restaurantId, tableCod, Language.PT, UUID.randomUUID());
    verify(orderRepository).delete(order2.getId());
  }

  @Test
  void shouldThrowExceptionWhenTableOrOrderNotFoundByCode() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T01";
    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.empty());

    var initiatedBy3 = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () -> closeTable.execute(restaurantId, tableCod, Language.PT, initiatedBy3));

    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, restaurantId, tableCod);
    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.empty());

    var initiatedBy4 = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () -> closeTable.execute(restaurantId, tableCod, Language.PT, initiatedBy4));
  }
}

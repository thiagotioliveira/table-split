package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloseTableTest {

  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private EventPublisher eventPublisher;
  private CloseTable closeTable;

  @BeforeEach
  void setUp() {
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    eventPublisher = mock(EventPublisher.class);
    closeTable = new CloseTable(tableRepository, orderRepository, eventPublisher);
  }

  @Test
  void shouldCloseTableSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    Table table = new Table(tableId, restaurantId, "T1");
    table.occupy();

    Order order = new Order(orderId, restaurantId, tableId, 10);
    // Add a ticket with item to make it "valid" (not garbage)
    dev.thiagooliveira.tablesplit.domain.order.Ticket ticket =
        new dev.thiagooliveira.tablesplit.domain.order.Ticket();
    dev.thiagooliveira.tablesplit.domain.order.TicketItem item =
        new dev.thiagooliveira.tablesplit.domain.order.TicketItem();
    item.setStatus(dev.thiagooliveira.tablesplit.domain.order.TicketStatus.PENDING);
    ticket.getItems().add(item);
    order.addTicket(ticket);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order result = closeTable.execute(orderId);

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
    // No tickets = invalid/garbage

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    closeTable.execute(orderId);

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
    // Add ticket with item and close
    dev.thiagooliveira.tablesplit.domain.order.Ticket ticket =
        new dev.thiagooliveira.tablesplit.domain.order.Ticket();
    dev.thiagooliveira.tablesplit.domain.order.TicketItem item =
        new dev.thiagooliveira.tablesplit.domain.order.TicketItem();
    item.setStatus(dev.thiagooliveira.tablesplit.domain.order.TicketStatus.PENDING);
    ticket.getItems().add(item);
    order.addTicket(ticket);
    order.close();

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order result = closeTable.execute(orderId);

    assertEquals(OrderStatus.CLOSED, result.getStatus());
    assertEquals(TableStatus.AVAILABLE, table.getStatus());
    verify(tableRepository).save(table);
  }
}

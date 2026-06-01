package dev.thiagooliveira.tablesplit.domain.order;

import static org.junit.jupiter.api.Assertions.*;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.event.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void shouldNotChangeTicketStatusWhenClosingOrder() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PREPARING);

    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    item.setStatus(TicketStatus.PREPARING);
    ticket.setItems(List.of(item));

    order.setTickets(List.of(ticket));

    order.close(Language.PT, UUID.randomUUID());

    assertEquals(OrderStatus.CLOSED, order.getStatus());
    assertEquals(TicketStatus.PREPARING, ticket.getStatus());
    assertEquals(TicketStatus.PREPARING, item.getStatus());
  }

  @Test
  void shouldRegisterTableOpenedEventOnOpen() {
    Table table = new Table(UUID.randomUUID(), UUID.randomUUID(), "T1");
    Order order = Order.open(table, 10);

    assertEquals(
        1, order.getDomainEvents().stream().filter(e -> e instanceof TableOpenedEvent).count());
  }

  @Test
  void shouldRegisterTicketCreatedEventOnAddTicketWithItems() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    order.addCustomer(UUID.randomUUID(), "Customer 1");

    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    List<TicketItem> items = List.of(item);
    order.addTicketWithItems(items, "Note", "T1", UUID.randomUUID(), Language.PT);

    assertEquals(
        1, order.getDomainEvents().stream().filter(e -> e instanceof TicketCreatedEvent).count());
  }

  @Test
  void shouldRegisterTableCreatedEventOnCreate() {
    Table table = Table.create(UUID.randomUUID(), UUID.randomUUID(), "T1");
    assertEquals(
        1, table.getDomainEvents().stream().filter(e -> e instanceof TableCreatedEvent).count());
  }

  @Test
  void shouldRegisterTableStatusChangedEventOnOccupy() {
    Table table = new Table(UUID.randomUUID(), UUID.randomUUID(), "T1");
    table.occupy();
    assertEquals(
        1,
        table.getDomainEvents().stream().filter(e -> e instanceof TableStatusChangedEvent).count());
  }

  @Test
  void shouldCloseOrderWhenFullyPaidEvenWithMinorPrecisionDifferences() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);

    TicketItem item = new TicketItem();
    item.setUnitPrice(new BigDecimal("10.55"));
    item.setQuantity(1);

    Ticket ticket = new Ticket();
    ticket.setItems(List.of(item));
    order.setTickets(List.of(ticket));

    BigDecimal almostFullPayment = new BigDecimal("11.609999999999999");

    Payment payment =
        new Payment(
            UUID.randomUUID(), order.getId(), null, almostFullPayment, PaymentMethod.CASH, null);

    order.addPayment(payment, Language.PT, UUID.randomUUID());
    order.close(Language.PT, UUID.randomUUID());

    assertEquals(
        OrderStatus.CLOSED, order.getStatus(), "Order should be closed due to rounding tolerance");
  }

  @Test
  void shouldCreateTakeawayOrderSuccessfully() {
    Order order = Order.takeaway(UUID.randomUUID(), 5);
    assertNull(order.getTableId());
    assertEquals(OrderStatus.OPEN, order.getStatus());
    assertEquals(5, order.getServiceFee());
  }

  @Test
  void shouldThrowExceptionWhenAddingPaymentToClosedOrCancelledOrder() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    order.setStatus(OrderStatus.CLOSED);
    Payment p =
        new Payment(
            UUID.randomUUID(), order.getId(), null, BigDecimal.TEN, PaymentMethod.CASH, null);
    var initiatedBy = UUID.randomUUID();
    assertThrows(
        IllegalOrderStatusException.class, () -> order.addPayment(p, Language.PT, initiatedBy));

    order.setStatus(OrderStatus.CANCELLED);
    var initiatedBy2 = UUID.randomUUID();
    assertThrows(
        IllegalOrderStatusException.class, () -> order.addPayment(p, Language.PT, initiatedBy2));
  }

  @Test
  void shouldThrowExceptionWhenPaymentExceedsRemainingAmount() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    Ticket ticket = new Ticket();
    ticket.setItems(List.of(item));
    order.setTickets(List.of(ticket));

    Payment p =
        new Payment(
            UUID.randomUUID(),
            order.getId(),
            null,
            BigDecimal.valueOf(100),
            PaymentMethod.CASH,
            null);
    var initiatedBy3 = UUID.randomUUID();
    assertThrows(OverpaymentException.class, () -> order.addPayment(p, Language.PT, initiatedBy3));
  }

  @Test
  void shouldThrowExceptionWhenRemovingPaymentFromCancelledOrder() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    order.setStatus(OrderStatus.CANCELLED);
    var removedId = UUID.randomUUID();
    assertThrows(IllegalOrderStatusException.class, () -> order.removePayment(removedId));
  }

  @Test
  void shouldThrowExceptionWhenAddingTicketToNonOpenOrder() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    order.setStatus(OrderStatus.CLOSED);
    Ticket ticket = new Ticket();
    assertThrows(IllegalOrderStatusException.class, () -> order.addTicket(ticket));
  }

  @Test
  void shouldCloseWithTableCorrectly() {
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, UUID.randomUUID(), "T01");
    table.occupy();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);
    order.close(table, Language.PT, UUID.randomUUID());

    assertEquals(OrderStatus.CLOSED, order.getStatus());
    assertTrue(table.isAvailable());
  }

  @Test
  void shouldCalculateSubtotalByCustomerCorrectly() {
    UUID customerId = UUID.randomUUID();
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);

    TicketItem item1 = new TicketItem();
    item1.setUnitPrice(BigDecimal.TEN);
    item1.setQuantity(2);
    item1.setCustomerId(customerId);

    TicketItem item2 = new TicketItem();
    item2.setUnitPrice(BigDecimal.valueOf(5));
    item2.setQuantity(1);
    item2.setCustomerId(null); // table level

    Ticket ticket = new Ticket();
    ticket.setItems(List.of(item1, item2));
    order.setTickets(List.of(ticket));

    assertEquals(BigDecimal.valueOf(20), order.calculateSubtotalByCustomer(customerId));
    assertEquals(BigDecimal.valueOf(5), order.calculateSubtotalByCustomer(null));
  }

  @Test
  void shouldUpdateCustomerNameAndVerifyParticipant() {
    UUID customerId = UUID.randomUUID();
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    order.addCustomer(customerId, "John");

    assertTrue(order.hasParticipant(customerId));
    assertFalse(order.hasParticipant(UUID.randomUUID()));
    assertFalse(order.hasParticipant(null));

    order.updateCustomerName(customerId, "John Doe");
    assertEquals("John Doe", order.getCustomerName(customerId).orElse(""));
  }

  @Test
  void shouldUpdateTicketItemStatusSuccessfully() {
    UUID ticketId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);

    TicketItem item = new TicketItem();
    item.setId(itemId);
    item.setQuantity(1);
    item.setUnitPrice(BigDecimal.TEN);
    ticket.setItems(new ArrayList<>(List.of(item)));

    order.setTickets(new ArrayList<>(List.of(ticket)));

    order.updateTicketItemStatus(ticketId, itemId, TicketStatus.DELIVERED);

    assertEquals(TicketStatus.DELIVERED, item.getStatus());
  }

  @Test
  void shouldCancelTicketItemFullyAndPartially() {
    UUID ticketId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);

    TicketItem item = new TicketItem();
    item.setId(itemId);
    item.setQuantity(3);
    item.setUnitPrice(BigDecimal.TEN);
    ticket.setItems(new ArrayList<>(List.of(item)));

    order.setTickets(new ArrayList<>(List.of(ticket)));

    // Partial cancellation of 1 item
    order.cancelTicketItem(ticketId, itemId, 1, Optional.of(CancellationReason.CUSTOMER_GAVE_UP));
    assertEquals(2, item.getQuantity());
    assertEquals(2, ticket.getItems().size()); // 1 original + 1 cancelled item entry

    // Full cancellation of remaining items
    order.cancelTicketItem(ticketId, itemId, 2, Optional.of(CancellationReason.OUT_OF_STOCK));
    assertEquals(TicketStatus.CANCELLED, item.getStatus());
  }

  @Test
  void shouldMoveTicketStatusSuccessfully() {
    UUID ticketId = UUID.randomUUID();
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);

    TicketItem item = new TicketItem();
    item.setQuantity(1);
    item.setUnitPrice(BigDecimal.TEN);
    item.setStatus(TicketStatus.PENDING);
    ticket.setItems(new ArrayList<>(List.of(item)));

    order.setTickets(new ArrayList<>(List.of(ticket)));

    order.moveTicket(ticketId, TicketStatus.READY);
    assertEquals(TicketStatus.READY, ticket.getStatus());
    assertEquals(TicketStatus.READY, item.getStatus());

    order.moveTicket(ticketId, TicketStatus.DELIVERED);
    assertEquals(TicketStatus.DELIVERED, ticket.getStatus());
    assertEquals(TicketStatus.DELIVERED, item.getStatus());
  }

  @Test
  void shouldTransferTableSuccessfully() {
    UUID sourceId = UUID.randomUUID();
    UUID targetId = UUID.randomUUID();

    Table sourceTable = new Table(sourceId, UUID.randomUUID(), "T01");
    sourceTable.occupy();
    Table targetTable = new Table(targetId, UUID.randomUUID(), "T02");

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), sourceId, 10);

    order.transfer(sourceTable, targetTable);

    assertEquals(targetId, order.getTableId());
    assertTrue(sourceTable.isAvailable());
    assertEquals(TableStatus.OCCUPIED, targetTable.getStatus());
  }

  @Test
  void shouldThrowExceptionWhenTransferringInvalidTable() {
    Table sourceTable = new Table(UUID.randomUUID(), UUID.randomUUID(), "T01");
    Table targetTable = new Table(UUID.randomUUID(), UUID.randomUUID(), "T02");
    targetTable.occupy();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);

    // Source table does not match order table
    assertThrows(IllegalArgumentException.class, () -> order.transfer(sourceTable, targetTable));

    Order takeaway = Order.takeaway(UUID.randomUUID(), 10);
    assertThrows(IllegalArgumentException.class, () -> takeaway.transfer(sourceTable, targetTable));
  }

  @Test
  void shouldCheckWaitingAndInvalidTickets() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    assertTrue(order.isInvalid());

    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PENDING);
    TicketItem item = new TicketItem();
    item.setStatus(TicketStatus.PENDING);
    ticket.setItems(new ArrayList<>(List.of(item)));
    order.setTickets(new ArrayList<>(List.of(ticket)));

    assertFalse(order.isInvalid());
    assertTrue(order.hasWaitingTickets());
  }

  @Test
  void shouldAddTicketWithItemsAddingDefaultCustomerWhenNoneExists() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    TicketItem item = new TicketItem();
    item.setQuantity(1);
    item.setUnitPrice(BigDecimal.TEN);

    order.addTicketWithItems(List.of(item), "Note", "T01", UUID.randomUUID(), Language.PT);

    assertEquals(1, order.getCustomers().size());
    assertNotNull(item.getCustomerId());
  }
}

package dev.thiagooliveira.tablesplit.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.thiagooliveira.tablesplit.domain.order.event.TableCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TableOpenedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TicketCreatedEvent;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void shouldNotChangeTicketStatusWhenClosingOrder() {
    // Given
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PREPARING);

    TicketItem item = new TicketItem();
    item.setStatus(TicketStatus.PREPARING);
    ticket.setItems(List.of(item));

    order.setTickets(List.of(ticket));

    Table table = new Table(UUID.randomUUID(), UUID.randomUUID(), "T1");
    // When
    order.close();

    // Then
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

    List<TicketItem> items = List.of(new TicketItem());
    order.addTicketWithItems(items, "Note", "T1");

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
    // Given
    // Suppose subtotal is 10.55 and service fee is 10%.
    // subtotal * 0.1 = 1.055. Rounded to 2 decimals is 1.06.
    // Total should be 10.55 + 1.06 = 11.61.

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);

    // Create an item that results in 10.55 subtotal
    TicketItem item = new TicketItem();
    item.setUnitPrice(new java.math.BigDecimal("10.55"));
    item.setQuantity(1);

    Ticket ticket = new Ticket();
    ticket.setItems(java.util.List.of(item));
    order.setTickets(java.util.List.of(ticket));

    // Total is 11.61.
    // Suppose the frontend has a precision issue and pays 11.609999999999999
    java.math.BigDecimal almostFullPayment = new java.math.BigDecimal("11.609999999999999");

    Payment payment =
        new Payment(
            UUID.randomUUID(), order.getId(), null, almostFullPayment, PaymentMethod.CASH, null);

    // When
    order.addPayment(payment);

    // Then
    assertEquals(
        OrderStatus.CLOSED, order.getStatus(), "Order should be closed due to rounding tolerance");
  }
}

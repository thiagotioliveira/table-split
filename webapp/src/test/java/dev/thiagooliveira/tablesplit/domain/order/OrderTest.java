package dev.thiagooliveira.tablesplit.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        1,
        order.getDomainEvents().stream()
            .filter(e -> e instanceof dev.thiagooliveira.tablesplit.domain.event.TableOpenedEvent)
            .count());
  }

  @Test
  void shouldRegisterTicketCreatedEventOnAddTicketWithItems() {
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10);
    order.addCustomer(UUID.randomUUID(), "Customer 1");

    List<TicketItem> items = List.of(new TicketItem());
    order.addTicketWithItems(items, "Note", "T1");

    assertEquals(
        1,
        order.getDomainEvents().stream()
            .filter(e -> e instanceof dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent)
            .count());
  }

  @Test
  void shouldRegisterTableCreatedEventOnCreate() {
    Table table = Table.create(UUID.randomUUID(), UUID.randomUUID(), "T1");
    assertEquals(
        1,
        table.getDomainEvents().stream()
            .filter(e -> e instanceof dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent)
            .count());
  }

  @Test
  void shouldRegisterTableStatusChangedEventOnOccupy() {
    Table table = new Table(UUID.randomUUID(), UUID.randomUUID(), "T1");
    table.occupy();
    assertEquals(
        1,
        table.getDomainEvents().stream()
            .filter(
                e ->
                    e instanceof dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent)
            .count());
  }
}

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

    // When
    order.close();

    // Then
    assertEquals(OrderStatus.CLOSED, order.getStatus());
    assertEquals(TicketStatus.PREPARING, ticket.getStatus());
    assertEquals(TicketStatus.PREPARING, item.getStatus());
  }
}

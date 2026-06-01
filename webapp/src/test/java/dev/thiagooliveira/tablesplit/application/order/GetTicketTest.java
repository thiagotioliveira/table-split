package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.*;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetTicketTest {

  private OrderRepository orderRepository;
  private TableRepository tableRepository;
  private GetTicket getTicket;

  @BeforeEach
  void setUp() {
    orderRepository = mock(OrderRepository.class);
    tableRepository = mock(TableRepository.class);
    getTicket = new GetTicket(orderRepository, tableRepository);
  }

  @Test
  void shouldRetrieveTicketWithTableSuccessfully() {
    UUID ticketId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);
    order.setTickets(Collections.singletonList(ticket));

    Table table = new Table(tableId, UUID.randomUUID(), "T10");

    when(orderRepository.findByTicketId(ticketId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Optional<TicketWithTable> result = getTicket.execute(ticketId);

    assertTrue(result.isPresent());
    assertEquals(ticketId, result.get().ticket().getId());
    assertEquals("T10", result.get().tableCod());
  }

  @Test
  void shouldThrowExceptionWhenTableNotFoundForTableId() {
    UUID ticketId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);
    order.setTickets(Collections.singletonList(ticket));

    when(orderRepository.findByTicketId(ticketId)).thenReturn(Optional.of(order));
    when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

    assertThrows(IllegalStateException.class, () -> getTicket.execute(ticketId));
  }
}

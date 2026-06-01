package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateTicketItemStatusTest {

  private OrderRepository orderRepository;
  private SyncTableStatus syncTableStatus;
  private UpdateTicketItemStatus updateTicketItemStatus;

  @BeforeEach
  void setUp() {
    orderRepository = mock(OrderRepository.class);
    syncTableStatus = mock(SyncTableStatus.class);
    updateTicketItemStatus = new UpdateTicketItemStatus(orderRepository, syncTableStatus);
  }

  @Test
  void shouldUpdateTicketItemStatusSuccessfully() {
    UUID itemId = UUID.randomUUID();
    UUID ticketId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    Order order = new Order(orderId, UUID.randomUUID(), UUID.randomUUID(), 10);
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);
    TicketItem item = new TicketItem();
    item.setId(itemId);
    item.setStatus(TicketStatus.PENDING);
    ticket.getItems().add(item);
    order.addTicket(ticket);

    when(orderRepository.findByTicketItemId(itemId)).thenReturn(Optional.of(order));

    updateTicketItemStatus.execute(itemId, TicketStatus.DELIVERED);

    assertEquals(TicketStatus.DELIVERED, item.getStatus());
    verify(orderRepository).save(order);
    verify(syncTableStatus).execute(order);
  }

  @Test
  void shouldThrowExceptionWhenItemNotFound() {
    UUID itemId = UUID.randomUUID();
    when(orderRepository.findByTicketItemId(itemId)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> updateTicketItemStatus.execute(itemId, TicketStatus.DELIVERED));
  }
}

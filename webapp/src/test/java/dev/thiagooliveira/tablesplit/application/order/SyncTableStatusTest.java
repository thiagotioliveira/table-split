package dev.thiagooliveira.tablesplit.application.order;

import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncTableStatusTest {

  @Mock private TableRepository tableRepository;
  @Mock private EventPublisher eventPublisher;

  private SyncTableStatus syncTableStatus;

  @BeforeEach
  void setUp() {
    syncTableStatus = new SyncTableStatus(tableRepository, eventPublisher);
  }

  @Test
  void shouldNotUpdateTableStatusWhenOrderIsClosed() {
    // Given
    UUID tableId = UUID.randomUUID();
    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);
    order.setStatus(OrderStatus.CLOSED);

    // When
    syncTableStatus.execute(order);

    // Then
    verifyNoInteractions(tableRepository);
    verifyNoInteractions(eventPublisher);
  }

  @Test
  void shouldSetTableToWaitingWhenOrderHasWaitingTickets() {
    // Given
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, UUID.randomUUID(), "1");
    table.setStatus(TableStatus.AVAILABLE);

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);
    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PENDING);
    order.setTickets(List.of(ticket));

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    // When
    syncTableStatus.execute(order);

    // Then
    verify(tableRepository).save(table);
    assert (table.getStatus() == TableStatus.WAITING);
  }
}

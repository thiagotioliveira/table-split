package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
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
class GetTicketsTest {

  @Mock private OrderRepository orderRepository;
  @Mock private TableRepository tableRepository;

  private GetTickets getTickets;
  private UUID restaurantId;

  @BeforeEach
  void setUp() {
    getTickets = new GetTickets(orderRepository, tableRepository);
    restaurantId = UUID.randomUUID();
  }

  @Test
  void shouldIncludeClosedOrdersWithWaitingTickets() {
    // Given
    UUID tableId = UUID.randomUUID();
    Table table = new Table();
    table.setId(tableId);
    table.setCod("1");
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order openOrder = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    Ticket t1 = new Ticket();
    t1.setStatus(TicketStatus.PENDING);
    openOrder.setTickets(List.of(t1));

    Order closedOrder = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    closedOrder.setStatus(OrderStatus.CLOSED);
    Ticket t2 = new Ticket();
    t2.setStatus(TicketStatus.PREPARING);
    TicketItem item = new TicketItem();
    item.setStatus(TicketStatus.PREPARING);
    t2.setItems(List.of(item));
    closedOrder.setTickets(List.of(t2));

    when(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN))
        .thenReturn(List.of(openOrder));
    when(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.CLOSED))
        .thenReturn(List.of(closedOrder));

    // When
    List<GetTickets.TicketWithTable> results = getTickets.execute(restaurantId, null);

    // Then
    assertEquals(2, results.size());
  }

  @Test
  void shouldNotIncludeClosedOrdersWithoutWaitingTickets() {
    // Given
    UUID tableId = UUID.randomUUID();
    Table table = new Table();
    table.setId(tableId);
    table.setCod("1");
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    Order openOrder = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    Ticket t1 = new Ticket();
    t1.setStatus(TicketStatus.PENDING);
    openOrder.setTickets(List.of(t1));

    Order closedOrder = new Order(UUID.randomUUID(), restaurantId, tableId, 10);
    closedOrder.setStatus(OrderStatus.CLOSED);
    Ticket t2 = new Ticket();
    t2.setStatus(TicketStatus.DELIVERED);
    TicketItem item = new TicketItem();
    item.setStatus(TicketStatus.DELIVERED);
    t2.setItems(List.of(item));
    closedOrder.setTickets(List.of(t2));

    when(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN))
        .thenReturn(List.of(openOrder));
    when(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.CLOSED))
        .thenReturn(List.of(closedOrder));

    // When
    List<GetTickets.TicketWithTable> results = getTickets.execute(restaurantId, null);

    // Then
    assertEquals(1, results.size());
    assertEquals(t1.getId(), results.get(0).ticket().getId());
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeletePaymentTest {

  private OrderRepository orderRepository;
  private TableRepository tableRepository;
  private DeletePayment deletePayment;

  @BeforeEach
  void setUp() {
    orderRepository = mock(OrderRepository.class);
    tableRepository = mock(TableRepository.class);
    deletePayment = new DeletePayment(orderRepository, tableRepository);
  }

  @Test
  void shouldDeletePaymentSuccessfully() {
    UUID tableId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);

    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(order));

    deletePayment.execute(tableId, paymentId);

    verify(orderRepository).save(order);
  }

  @Test
  void shouldReoccupyTableWhenOrderStatusTransitionsBackToOpen() {
    UUID tableId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();

    Order realOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10);
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.TEN);
    item.setQuantity(1);
    Ticket ticket = new Ticket();
    ticket.setItems(List.of(item));
    realOrder.setTickets(List.of(ticket));

    Payment payment =
        new Payment(
            paymentId,
            realOrder.getId(),
            null,
            BigDecimal.valueOf(11.00),
            PaymentMethod.CASH,
            null);
    realOrder.getPayments().add(payment);
    realOrder.setStatus(OrderStatus.CLOSED);

    Table table = new Table(tableId, UUID.randomUUID(), "T01");

    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(realOrder));
    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

    deletePayment.execute(tableId, paymentId);

    assertEquals(OrderStatus.OPEN, realOrder.getStatus());
    verify(orderRepository).save(realOrder);
    verify(tableRepository).save(table);
  }

  @Test
  void shouldThrowExceptionWhenNoActiveOrderFoundForTable() {
    UUID tableId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();

    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> deletePayment.execute(tableId, paymentId));

    verify(orderRepository, never()).save(any());
  }
}

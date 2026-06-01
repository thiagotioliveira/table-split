package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessPaymentTest {

  private OrderRepository orderRepository;
  private CloseTable closeTable;
  private ProcessPayment processPayment;

  @BeforeEach
  void setUp() {
    orderRepository = mock(OrderRepository.class);
    closeTable = mock(CloseTable.class);
    processPayment = new ProcessPayment(orderRepository, closeTable);
  }

  @Test
  void shouldProcessPartialPaymentSuccessfully() {
    UUID tableId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    UUID initiatedBy = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10); // 10% service fee

    // Setup order items so subtotal is 100.00 -> total is 110.00
    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.valueOf(100.00));
    item.setQuantity(1);

    Ticket ticket = new Ticket();
    ticket.setItems(Collections.singletonList(item));
    order.setTickets(Collections.singletonList(ticket));

    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(order));

    BigDecimal paymentAmount = BigDecimal.valueOf(50.00);

    Order result =
        processPayment.execute(
            tableId,
            customerId,
            paymentAmount,
            PaymentMethod.CASH,
            "Partial note",
            Language.PT,
            initiatedBy);

    assertNotNull(result);
    assertFalse(result.isFullyPaid());
    assertEquals(0, BigDecimal.valueOf(60.00).compareTo(result.calculateRemainingAmount()));

    verify(orderRepository).save(order);
    verify(closeTable, never()).execute(any(), any(), any());
  }

  @Test
  void shouldProcessFullPaymentAndTriggerTableClosure() {
    UUID tableId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    UUID initiatedBy = UUID.randomUUID();

    Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), tableId, 10); // 10% service fee

    TicketItem item = new TicketItem();
    item.setUnitPrice(BigDecimal.valueOf(100.00));
    item.setQuantity(1);

    Ticket ticket = new Ticket();
    ticket.setItems(Collections.singletonList(item));
    order.setTickets(Collections.singletonList(ticket));

    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.of(order));

    // Pay full total of 110.00
    BigDecimal paymentAmount = BigDecimal.valueOf(110.00);

    Order result =
        processPayment.execute(
            tableId,
            customerId,
            paymentAmount,
            PaymentMethod.CARD,
            "Full payment",
            Language.PT,
            initiatedBy);

    assertNotNull(result);
    assertTrue(result.isFullyPaid());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.calculateRemainingAmount()));

    verify(orderRepository).save(order);
    verify(closeTable).execute(order.getId(), Language.PT, initiatedBy);
  }

  @Test
  void shouldThrowExceptionWhenNoActiveOrderFoundForTable() {
    UUID tableId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    UUID initiatedBy = UUID.randomUUID();

    when(orderRepository.findActiveOrderByTableId(tableId)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            processPayment.execute(
                tableId,
                customerId,
                BigDecimal.TEN,
                PaymentMethod.CARD,
                null,
                Language.EN,
                initiatedBy));

    verify(orderRepository, never()).save(any());
    verify(closeTable, never()).execute(any(), any(), any());
  }
}

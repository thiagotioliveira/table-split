package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import java.math.BigDecimal;
import java.util.UUID;

public class ProcessPayment {

  private final OrderRepository orderRepository;
  private final CloseTable closeTable;

  public ProcessPayment(OrderRepository orderRepository, CloseTable closeTable) {
    this.orderRepository = orderRepository;
    this.closeTable = closeTable;
  }

  public Order execute(
      UUID tableId,
      UUID customerId,
      BigDecimal amount,
      dev.thiagooliveira.tablesplit.domain.order.PaymentMethod method,
      String note) {
    Order order =
        orderRepository
            .findActiveOrderByTableId(tableId)
            .orElseThrow(
                () -> new IllegalArgumentException("No active order found for table: " + tableId));

    Payment payment =
        new Payment(UUID.randomUUID(), order.getId(), customerId, amount, method, note);
    order.processPayment(payment);

    orderRepository.save(order);

    if (order.getStatus() == dev.thiagooliveira.tablesplit.domain.order.OrderStatus.CLOSED) {
      closeTable.execute(order.getId());
    }

    return order;
  }
}

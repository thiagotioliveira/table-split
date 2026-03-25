package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.PaymentProcessedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TableClosedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import java.math.BigDecimal;
import java.util.UUID;

public class ProcessPayment {

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;
  private final EventPublisher eventPublisher;

  public ProcessPayment(
      OrderRepository orderRepository,
      TableRepository tableRepository,
      EventPublisher eventPublisher) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
    this.eventPublisher = eventPublisher;
  }

  public Order execute(
      UUID tableId,
      String customerName,
      BigDecimal amount,
      dev.thiagooliveira.tablesplit.domain.order.PaymentMethod method,
      String note) {
    Order order =
        orderRepository
            .findActiveOrderByTableId(tableId)
            .orElseThrow(
                () -> new IllegalArgumentException("No active order found for table: " + tableId));

    Payment payment =
        new Payment(UUID.randomUUID(), order.getId(), customerName, amount, method, note);
    order.addPayment(payment);

    orderRepository.save(order);
    eventPublisher.publishEvent(new PaymentProcessedEvent(order, payment));

    if (order.getStatus() == dev.thiagooliveira.tablesplit.domain.order.OrderStatus.CLOSED) {
      tableRepository
          .findById(tableId)
          .ifPresent(
              table -> {
                table.release();
                tableRepository.save(table);
                eventPublisher.publishEvent(new TableClosedEvent(order, table));
              });
    }

    return order;
  }
}

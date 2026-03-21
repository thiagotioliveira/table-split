package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessPayment {

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;

  public ProcessPayment(OrderRepository orderRepository, TableRepository tableRepository) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
  }

  @Transactional
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

    if (order.getStatus() == dev.thiagooliveira.tablesplit.domain.order.OrderStatus.CLOSED) {
      tableRepository
          .findById(tableId)
          .ifPresent(
              table -> {
                table.release();
                tableRepository.save(table);
              });
    }

    return order;
  }
}

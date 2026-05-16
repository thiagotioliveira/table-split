package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessPayment {

  private static final Logger log = LoggerFactory.getLogger(ProcessPayment.class);

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
      String note,
      Language language,
      UUID initiatedBy) {
    Order order =
        orderRepository
            .findActiveOrderByTableId(tableId)
            .orElseThrow(
                () -> new IllegalArgumentException("No active order found for table: " + tableId));

    log.info(
        "Processing payment for table {}: amount={}, total={}, remaining before payment={}",
        tableId,
        amount,
        order.calculateTotal(),
        order.calculateRemainingAmount());

    Payment payment =
        new Payment(UUID.randomUUID(), order.getId(), customerId, amount, method, note);
    order.processPayment(payment, language, initiatedBy);

    orderRepository.save(order);

    if (order.isFullyPaid()) {
      log.info("Order for table {} is fully paid. Closing table.", tableId);
      closeTable.execute(order.getId(), language, initiatedBy);
    } else {
      log.info(
          "Order for table {} is not yet fully paid. Remaining: {}",
          tableId,
          order.calculateRemainingAmount());
    }

    return order;
  }
}

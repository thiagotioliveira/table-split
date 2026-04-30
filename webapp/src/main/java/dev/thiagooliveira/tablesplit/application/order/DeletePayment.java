package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.UUID;

public class DeletePayment {

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;

  public DeletePayment(OrderRepository orderRepository, TableRepository tableRepository) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
  }

  public void execute(UUID tableId, UUID paymentId) {
    Order order =
        orderRepository
            .findActiveOrderByTableId(tableId)
            .orElseThrow(
                () -> new IllegalArgumentException("No active order found for table: " + tableId));

    OrderStatus previousStatus = order.getStatus();
    order.removePayment(paymentId);

    orderRepository.save(order);

    if (previousStatus == OrderStatus.CLOSED && order.getStatus() == OrderStatus.OPEN) {
      tableRepository
          .findById(tableId)
          .ifPresent(
              table -> {
                table.occupy();
                tableRepository.save(table);
              });
    }

    // We could publish a PaymentDeletedEvent here if needed,
    // but the system will recalculate on the next load/refresh.
  }
}

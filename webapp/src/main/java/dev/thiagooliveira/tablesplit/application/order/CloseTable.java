package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.UUID;

public class CloseTable {

  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;

  public CloseTable(TableRepository tableRepository, OrderRepository orderRepository) {
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
  }

  public Order execute(UUID orderId, Language language, UUID initiatedBy) {
    var order =
        this.orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

    var table =
        this.tableRepository
            .findById(order.getTableId())
            .orElseThrow(
                () -> new IllegalArgumentException("Table not found: " + order.getTableId()));

    if (order.getTickets().isEmpty()) {
      table.release();
      this.orderRepository.delete(orderId);
    } else {
      order.close(table, language, initiatedBy);
      this.orderRepository.save(order);
    }

    this.tableRepository.save(table);

    return order;
  }

  public void execute(UUID restaurantId, String tableCod, Language language, UUID initiatedBy) {
    var table =
        this.tableRepository
            .findByRestaurantIdAndCod(restaurantId, tableCod)
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableCod));

    var order =
        this.orderRepository
            .findActiveOrderByTableId(table.getId())
            .orElseThrow(
                () -> new IllegalArgumentException("No active order for table: " + tableCod));

    if (order.getTickets().isEmpty()) {
      table.release();
      this.orderRepository.delete(order.getId());
    } else {
      order.close(table, language, initiatedBy);
      this.orderRepository.save(order);
    }

    this.tableRepository.save(table);
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.UUID;

public class TransferTable {

  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;

  public TransferTable(TableRepository tableRepository, OrderRepository orderRepository) {
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
  }

  public void execute(UUID sourceTableId, UUID targetTableId) {
    Table sourceTable =
        tableRepository
            .findById(sourceTableId)
            .orElseThrow(() -> new IllegalArgumentException("Source table not found"));
    Table targetTable =
        tableRepository
            .findById(targetTableId)
            .orElseThrow(() -> new IllegalArgumentException("Target table not found"));

    Order activeOrder =
        orderRepository
            .findActiveOrderByTableId(sourceTableId)
            .orElseThrow(() -> new IllegalStateException("No active order found for source table"));

    activeOrder.transfer(sourceTable, targetTable);

    tableRepository.save(sourceTable);
    tableRepository.save(targetTable);
    orderRepository.save(activeOrder);
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyOccupied;
import dev.thiagooliveira.tablesplit.domain.event.TableOpenedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.UUID;

public class OpenTable {

  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;

  public OpenTable(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      EventPublisher eventPublisher) {
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
  }

  public Order execute(UUID tableId) {
    Table table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    if (table.getStatus() == TableStatus.OCCUPIED) {
      throw new TableAlreadyOccupied();
    }

    table.occupy();
    tableRepository.save(table);

    Order order = new Order(UUID.randomUUID(), table.getRestaurantId(), table.getId());
    orderRepository.save(order);

    eventPublisher.publishEvent(new TableOpenedEvent(order, table));

    return order;
  }
}

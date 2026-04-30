package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.TableOpenedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.UUID;

public class OpenTable {

  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;
  private final SyncTableStatus syncTableStatus;

  public OpenTable(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus) {
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
    this.syncTableStatus = syncTableStatus;
  }

  public Order execute(UUID tableId, int serviceFee, UUID customerId, String customerName) {
    Table table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    if (table.getStatus() != TableStatus.AVAILABLE) {
      Order currentOrder =
          orderRepository
              .findActiveOrderByTableId(tableId)
              .orElseThrow(() -> new IllegalStateException("Table occupied but no active order"));

      if (customerId != null) {
        currentOrder.addCustomer(customerId, customerName);
        orderRepository.save(currentOrder);
        syncTableStatus.execute(currentOrder);
      }
      return currentOrder;
    }

    table.occupy();
    tableRepository.save(table);

    Order order = new Order(UUID.randomUUID(), table.getRestaurantId(), table.getId(), serviceFee);
    if (customerId != null) {
      order.addCustomer(customerId, customerName);
    }
    orderRepository.save(order);

    eventPublisher.publishEvent(new TableStatusChangedEvent(table));
    eventPublisher.publishEvent(new TableOpenedEvent(order, table));

    return order;
  }
}

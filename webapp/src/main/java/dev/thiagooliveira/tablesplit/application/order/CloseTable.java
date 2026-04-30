package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.TableClosedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class CloseTable {

  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;

  public CloseTable(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      EventPublisher eventPublisher) {
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
  }

  public Order execute(UUID orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    Table table = tableRepository.findById(order.getTableId()).orElseThrow();

    if (order.isInvalid()) {
      orderRepository.delete(order.getId());
      table.release();
      tableRepository.save(table);
      eventPublisher.publishEvent(new TableStatusChangedEvent(table));
      return order;
    }

    if (order.getStatus() != dev.thiagooliveira.tablesplit.domain.order.OrderStatus.CLOSED) {
      order.close();
      orderRepository.save(order);
    }

    table.release();
    tableRepository.save(table);

    eventPublisher.publishEvent(new TableStatusChangedEvent(table));
    eventPublisher.publishEvent(new TableClosedEvent(order, table));

    return order;
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;

public class SyncTableStatus {

  private final TableRepository tableRepository;
  private final EventPublisher eventPublisher;

  public SyncTableStatus(TableRepository tableRepository, EventPublisher eventPublisher) {
    this.tableRepository = tableRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(Order order) {
    Table table =
        tableRepository
            .findById(order.getTableId())
            .orElseThrow(() -> new IllegalStateException("Table not found: " + order.getTableId()));

    if (order.hasWaitingTickets()) {
      if (table.getStatus() != TableStatus.WAITING) {
        table.waiting();
        tableRepository.save(table);
        eventPublisher.publishEvent(new TableStatusChangedEvent(table));
      }
    } else {
      // If no waiting tickets, and it's not available, it must be occupied
      if (table.getStatus() == TableStatus.WAITING) {
        table.occupy();
        tableRepository.save(table);
        eventPublisher.publishEvent(new TableStatusChangedEvent(table));
      }
    }
  }
}

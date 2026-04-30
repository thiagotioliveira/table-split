package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;

public class SyncTableStatus {

  private final TableRepository tableRepository;

  public SyncTableStatus(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  public void execute(Order order) {
    var table = this.tableRepository.findById(order.getTableId()).orElseThrow();

    table.syncStatus(order.getStatus());
    this.tableRepository.save(table);
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.common.DomainException;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.UUID;

public class DeleteTable {

  private final TableRepository tableRepository;

  public DeleteTable(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  /**
   * Deletes a table:
   *
   * <ul>
   *   <li>Physical delete when the table has no associated orders.
   *   <li>Logical delete (soft-delete via {@code deletedAt}) when orders exist.
   * </ul>
   */
  public void execute(UUID tableId) {
    var table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new DomainException("Table not found: " + tableId));

    if (tableRepository.hasOrders(tableId)) {
      table.softDelete();
      tableRepository.save(table);
    } else {
      tableRepository.delete(tableId);
    }
  }
}

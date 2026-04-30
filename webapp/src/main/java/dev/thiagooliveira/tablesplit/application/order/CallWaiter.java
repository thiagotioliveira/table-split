package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.UUID;

public class CallWaiter {

  private final TableRepository tableRepository;

  public CallWaiter(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  public void execute(UUID restaurantId, String tableCod) {
    execute(restaurantId, tableCod, null);
  }

  public void execute(UUID restaurantId, String tableCod, UUID customerId) {
    var table =
        this.tableRepository
            .findByRestaurantIdAndCod(restaurantId, tableCod)
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableCod));

    table.callWaiter();
    this.tableRepository.save(table);
  }
}

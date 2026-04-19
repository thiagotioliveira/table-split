package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.WaiterCalledEvent;
import java.util.UUID;

public class CallWaiter {

  private final TableRepository tableRepository;
  private final EventPublisher eventPublisher;

  public CallWaiter(TableRepository tableRepository, EventPublisher eventPublisher) {
    this.tableRepository = tableRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID restaurantId, String tableCod) {
    execute(restaurantId, tableCod, null);
  }

  public void execute(UUID restaurantId, String tableCod, UUID customerId) {
    if (!tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod).isPresent()) {
      throw new IllegalArgumentException("Table not found: " + tableCod);
    }

    eventPublisher.publishEvent(new WaiterCalledEvent(restaurantId, tableCod));
  }
}

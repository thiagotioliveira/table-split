package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCallRepository;
import java.util.List;
import java.util.UUID;

public class ListActiveWaiterCalls {
  private final WaiterCallRepository repository;

  public ListActiveWaiterCalls(WaiterCallRepository repository) {
    this.repository = repository;
  }

  public List<WaiterCall> execute(UUID restaurantId) {
    return repository.findAllActiveByRestaurantId(restaurantId);
  }
}

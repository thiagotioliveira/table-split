package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCallRepository;
import java.util.UUID;

public class DismissWaiterCall {

  private final WaiterCallRepository repository;

  public DismissWaiterCall(WaiterCallRepository repository) {
    this.repository = repository;
  }

  public void execute(UUID id) {
    var waiterCallOptional = repository.findById(id);
    waiterCallOptional.ifPresent(
        waiterCall -> {
          long totalActive =
              repository.findAllActiveByRestaurantId(waiterCall.getRestaurantId()).size();
          // Decrement by 1 if we consider the current one as about to be dismissed
          // But the original code was calculating BEFORE saving, so let's check
          // Wait, the original code calculated totalActive BEFORE saving.
          // So it includes the one that is about to be dismissed?
          // Line 25-26 in previous version:
          // long totalActive =
          // repository.findAllActiveByRestaurantId(waiterCall.getRestaurantId()).size();
          // Then it published event with this value.

          waiterCall.dismiss(totalActive - 1); // We subtract 1 because this one is now dismissed
          repository.save(waiterCall);
        });
  }
}

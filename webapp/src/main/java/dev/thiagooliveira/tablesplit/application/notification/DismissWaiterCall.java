package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.util.Optional;
import java.util.UUID;

public class DismissWaiterCall {
  private final WaiterCallRepository repository;

  public DismissWaiterCall(WaiterCallRepository repository) {
    this.repository = repository;
  }

  public void execute(UUID id) {
    Optional<WaiterCall> waiterCallOptional = repository.findById(id);
    waiterCallOptional.ifPresent(
        waiterCall -> {
          waiterCall.dismiss();
          repository.save(waiterCall);
        });
  }
}

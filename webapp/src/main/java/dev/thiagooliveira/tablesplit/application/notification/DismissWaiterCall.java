package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;

public class DismissWaiterCall {
  private final WaiterCallRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  public DismissWaiterCall(
      WaiterCallRepository repository, ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID id) {
    Optional<WaiterCall> waiterCallOptional = repository.findById(id);
    waiterCallOptional.ifPresent(
        waiterCall -> {
          waiterCall.dismiss();
          repository.save(waiterCall);
          long totalActive =
              repository.findAllActiveByRestaurantId(waiterCall.getRestaurantId()).size();
          eventPublisher.publishEvent(
              new WaiterCallDismissedEvent(waiterCall.getRestaurantId(), id, totalActive));
        });
  }
}

package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.util.UUID;

public class RegisterWaiterCall {
  private final WaiterCallRepository repository;
  private final Broadcaster broadcaster;

  public RegisterWaiterCall(WaiterCallRepository repository, Broadcaster broadcaster) {
    this.repository = repository;
    this.broadcaster = broadcaster;
  }

  public WaiterCall execute(UUID restaurantId, String tableCod) {
    WaiterCall waiterCall =
        repository
            .findActiveByRestaurantIdAndTableCod(restaurantId, tableCod)
            .map(
                existing -> {
                  existing.incrementCount();
                  return existing;
                })
            .orElseGet(() -> new WaiterCall(UUID.randomUUID(), restaurantId, tableCod, Time.now()));

    repository.save(waiterCall);
    long totalActive = repository.findAllActiveByRestaurantId(restaurantId).size();

    String payload =
        String.format(
            "{\"id\": \"%s\", \"tableCod\": \"%s\", \"count\": %d, \"totalCount\": %d, \"title\": \"Atendimento - Mesa %s\", \"body\": \"A mesa %s está chamando o garçom\", \"url\": \"/tables\"}",
            waiterCall.getId(),
            tableCod,
            waiterCall.getCallCount(),
            totalActive,
            tableCod,
            tableCod);

    broadcaster.callWaiter(restaurantId, payload);
    return waiterCall;
  }
}

package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.time.ZonedDateTime;
import java.util.UUID;

public class RegisterWaiterCall {
  private final WaiterCallRepository repository;
  private final Broadcaster broadcaster;

  public RegisterWaiterCall(WaiterCallRepository repository, Broadcaster broadcaster) {
    this.repository = repository;
    this.broadcaster = broadcaster;
  }

  public void execute(UUID restaurantId, String tableCod) {
    WaiterCall waiterCall =
        new WaiterCall(UUID.randomUUID(), restaurantId, tableCod, ZonedDateTime.now());
    repository.save(waiterCall);

    String payload =
        String.format(
            "{\"id\": \"%s\", \"tableCod\": \"%s\", \"title\": \"Atendimento - Mesa %s\", \"body\": \"A mesa %s está chamando o garçom\", \"url\": \"/orders\"}",
            waiterCall.getId(), tableCod, tableCod, tableCod);

    broadcaster.callWaiter(restaurantId, payload);
  }
}

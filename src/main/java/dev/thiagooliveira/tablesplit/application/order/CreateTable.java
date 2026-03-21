package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class CreateTable {

  private final TableRepository tableRepository;
  private final EventPublisher eventPublisher;

  public CreateTable(TableRepository tableRepository, EventPublisher eventPublisher) {
    this.tableRepository = tableRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID restaurantId, String cod) {
    tableRepository
        .findByRestaurantIdAndCod(restaurantId, cod)
        .ifPresent(
            table -> {
              throw new TableAlreadyExists("Table already exists with cod: " + cod);
            });

    Table table = new Table(UUID.randomUUID(), restaurantId, cod);
    tableRepository.save(table);
    eventPublisher.publishEvent(new TableCreatedEvent(table));
  }
}

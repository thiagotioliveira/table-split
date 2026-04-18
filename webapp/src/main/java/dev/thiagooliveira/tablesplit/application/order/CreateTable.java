package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitType;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class CreateTable {

  private final TableRepository tableRepository;
  private final EventPublisher eventPublisher;
  private final PlanLimitValidator planLimitValidator;

  public CreateTable(
      TableRepository tableRepository,
      EventPublisher eventPublisher,
      PlanLimitValidator planLimitValidator) {
    this.tableRepository = tableRepository;
    this.eventPublisher = eventPublisher;
    this.planLimitValidator = planLimitValidator;
  }

  public void execute(UUID restaurantId, String cod) {
    this.planLimitValidator.validateByRestaurantId(
        restaurantId, PlanLimitType.TABLES, this.tableRepository.count(restaurantId));

    // Check if a non-deleted table already has this cod
    tableRepository
        .findByRestaurantIdAndCod(restaurantId, cod)
        .ifPresent(
            t -> {
              throw new TableAlreadyExists("Table already exists with cod: " + cod);
            });

    // Resurrect a previously soft-deleted table if one exists
    var existing = tableRepository.findByRestaurantIdAndCodIncludingDeleted(restaurantId, cod);
    if (existing.isPresent()) {
      var table = existing.get();
      table.restore();
      tableRepository.save(table);
      eventPublisher.publishEvent(new TableCreatedEvent(table));
      return;
    }

    // Brand-new table
    Table table = new Table(UUID.randomUUID(), restaurantId, cod);
    tableRepository.save(table);
    eventPublisher.publishEvent(new TableCreatedEvent(table));
  }
}

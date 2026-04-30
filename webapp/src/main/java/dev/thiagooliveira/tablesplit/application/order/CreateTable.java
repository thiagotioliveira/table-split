package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitType;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class CreateTable {

  private final TableRepository tableRepository;
  private final PlanLimitValidator planLimitValidator;

  public CreateTable(TableRepository tableRepository, PlanLimitValidator planLimitValidator) {
    this.tableRepository = tableRepository;
    this.planLimitValidator = planLimitValidator;
  }

  public Table execute(UUID accountId, UUID restaurantId, String cod) {
    this.planLimitValidator.validate(
        accountId, PlanLimitType.TABLES, this.tableRepository.count(restaurantId));

    if (this.tableRepository.findByRestaurantIdAndCod(restaurantId, cod).isPresent()) {
      throw new TableAlreadyExists(cod);
    }

    var existingDeletedTable =
        this.tableRepository.findByRestaurantIdAndCodIncludingDeleted(restaurantId, cod);
    if (existingDeletedTable.isPresent()) {
      var table = existingDeletedTable.get();
      table.restore();
      this.tableRepository.save(table);
      return table;
    }

    var table = Table.create(restaurantId, cod);
    this.tableRepository.save(table);

    return table;
  }
}

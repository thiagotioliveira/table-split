package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetTables {

  private final TableRepository tableRepository;

  public GetTables(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  public Optional<Table> findById(UUID id) {
    return tableRepository.findById(id);
  }

  public Result execute(UUID restaurantId) {
    List<Table> tables = tableRepository.findAllByRestaurantId(restaurantId);
    long count = tables.size();
    long countAvailable =
        tables.stream()
            .filter(
                table ->
                    table.getStatus()
                        == dev.thiagooliveira.tablesplit.domain.order.TableStatus.AVAILABLE)
            .count();
    long countOccupied =
        tables.stream()
            .filter(
                table ->
                    table.getStatus()
                        == dev.thiagooliveira.tablesplit.domain.order.TableStatus.OCCUPIED)
            .count();

    return new Result(tables, count, countAvailable, countOccupied);
  }

  public record Result(List<Table> tables, long count, long countAvailable, long countOccupied) {}
}

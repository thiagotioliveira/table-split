package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableRepository {
  Optional<Table> findById(UUID id);

  Optional<Table> findByRestaurantIdAndCod(UUID restaurantId, String cod);

  List<Table> findAllByRestaurantId(UUID restaurantId);

  void save(Table table);
}

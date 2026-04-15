package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableRepository {

  Optional<Table> findById(UUID id);

  /** Finds only non-deleted tables matching restaurantId + cod. */
  Optional<Table> findByRestaurantIdAndCod(UUID restaurantId, String cod);

  /** Finds any table (including soft-deleted) matching restaurantId + cod. */
  Optional<Table> findByRestaurantIdAndCodIncludingDeleted(UUID restaurantId, String cod);

  List<Table> findAllByRestaurantId(UUID restaurantId);

  boolean hasOrders(UUID tableId);

  void save(Table table);

  void delete(UUID tableId);
}

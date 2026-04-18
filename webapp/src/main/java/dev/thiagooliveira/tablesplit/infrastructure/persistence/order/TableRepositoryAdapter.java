package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.application.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TableRepositoryAdapter implements TableRepository {

  private final TableJpaRepository tableJpaRepository;
  private final OrderJpaRepository orderJpaRepository;

  public TableRepositoryAdapter(
      TableJpaRepository tableJpaRepository, OrderJpaRepository orderJpaRepository) {
    this.tableJpaRepository = tableJpaRepository;
    this.orderJpaRepository = orderJpaRepository;
  }

  @Override
  public Optional<Table> findById(UUID id) {
    return tableJpaRepository.findById(id).map(TableEntity::toDomain);
  }

  @Override
  public Optional<Table> findByRestaurantIdAndCod(UUID restaurantId, String cod) {
    return tableJpaRepository
        .findByRestaurantIdAndCodAndDeletedAtIsNull(restaurantId, cod)
        .map(TableEntity::toDomain);
  }

  @Override
  public Optional<Table> findByRestaurantIdAndCodIncludingDeleted(UUID restaurantId, String cod) {
    return tableJpaRepository
        .findByRestaurantIdAndCod(restaurantId, cod)
        .map(TableEntity::toDomain);
  }

  @Override
  public List<Table> findAllByRestaurantId(UUID restaurantId) {
    return tableJpaRepository
        .findAllByRestaurantIdAndDeletedAtIsNullOrderByCod(restaurantId)
        .stream()
        .map(TableEntity::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean hasOrders(UUID tableId) {
    return orderJpaRepository.existsByTableId(tableId);
  }

  @Override
  public void save(Table table) {
    tableJpaRepository.save(TableEntity.fromDomain(table));
  }

  @Override
  public void delete(UUID tableId) {
    tableJpaRepository.deleteById(tableId);
  }

  @Override
  public long count(UUID restaurantId) {
    return tableJpaRepository.countByRestaurantIdAndDeletedAtIsNull(restaurantId);
  }
}

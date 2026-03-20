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

  public TableRepositoryAdapter(TableJpaRepository tableJpaRepository) {
    this.tableJpaRepository = tableJpaRepository;
  }

  @Override
  public Optional<Table> findById(UUID id) {
    return tableJpaRepository.findById(id).map(TableEntity::toDomain);
  }

  @Override
  public Optional<Table> findByRestaurantIdAndCod(UUID restaurantId, String cod) {
    return tableJpaRepository
        .findByRestaurantIdAndCod(restaurantId, cod)
        .map(TableEntity::toDomain);
  }

  @Override
  public List<Table> findAllByRestaurantId(UUID restaurantId) {
    return tableJpaRepository.findAllByRestaurantId(restaurantId).stream()
        .map(TableEntity::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void save(Table table) {
    tableJpaRepository.save(TableEntity.fromDomain(table));
  }
}

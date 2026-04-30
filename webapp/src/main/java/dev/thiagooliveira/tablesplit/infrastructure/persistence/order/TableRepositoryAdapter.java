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
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant
          .RestaurantJpaRepository
      restaurantJpaRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;

  public TableRepositoryAdapter(
      TableJpaRepository tableJpaRepository,
      OrderJpaRepository orderJpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository
          restaurantJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher) {
    this.tableJpaRepository = tableJpaRepository;
    this.orderJpaRepository = orderJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<Table> findById(UUID id) {
    return tableJpaRepository.findById(id).map(this::toDomainWithAccount);
  }

  private Table toDomainWithAccount(TableEntity entity) {
    Table table = entity.toDomain();
    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            table.getRestaurantId());
    if (cachedAccountId != null) {
      table.setAccountId(cachedAccountId);
    } else {
      this.restaurantJpaRepository
          .findById(table.getRestaurantId())
          .ifPresent(r -> table.setAccountId(r.getAccountId()));
    }
    return table;
  }

  @Override
  public Optional<Table> findByRestaurantIdAndCod(UUID restaurantId, String cod) {
    return tableJpaRepository
        .findByRestaurantIdAndCodAndDeletedAtIsNull(restaurantId, cod)
        .map(this::toDomainWithAccount);
  }

  @Override
  public Optional<Table> findByRestaurantIdAndCodIncludingDeleted(UUID restaurantId, String cod) {
    return tableJpaRepository
        .findByRestaurantIdAndCod(restaurantId, cod)
        .map(this::toDomainWithAccount);
  }

  @Override
  public List<Table> findAllByRestaurantId(UUID restaurantId) {
    return tableJpaRepository
        .findAllByRestaurantIdAndDeletedAtIsNullOrderByCod(restaurantId)
        .stream()
        .map(this::toDomainWithAccount)
        .collect(Collectors.toList());
  }

  @Override
  public boolean hasOrders(UUID tableId) {
    return orderJpaRepository.existsByTableId(tableId);
  }

  @Override
  public void save(Table table) {
    tableJpaRepository.save(TableEntity.fromDomain(table));

    // Ensure accountId is populated for events if it's not already
    if (table.getAccountId() == null) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              table.getRestaurantId());
      table.setAccountId(cachedAccountId);
    }

    // Publish Domain Events
    table.getDomainEvents().forEach(eventPublisher::publishEvent);
    table.clearEvents();
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

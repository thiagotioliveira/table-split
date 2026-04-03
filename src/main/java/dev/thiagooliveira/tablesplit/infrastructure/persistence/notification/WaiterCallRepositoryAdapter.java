package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.application.notification.WaiterCallRepository;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class WaiterCallRepositoryAdapter implements WaiterCallRepository {

  private final WaiterCallJpaRepository jpaRepository;

  public WaiterCallRepositoryAdapter(WaiterCallJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(WaiterCall waiterCall) {
    WaiterCallEntity entity = toEntity(waiterCall);
    jpaRepository.save(entity);
  }

  @Override
  public List<WaiterCall> findAllActiveByRestaurantId(UUID restaurantId) {
    return jpaRepository
        .findAllByRestaurantIdAndDismissedAtIsNullOrderByCreatedAtDesc(restaurantId)
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<WaiterCall> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<WaiterCall> findActiveByRestaurantIdAndTableCod(
      UUID restaurantId, String tableCod) {
    return jpaRepository
        .findByRestaurantIdAndTableCodAndDismissedAtIsNull(restaurantId, tableCod)
        .map(this::toDomain);
  }

  private WaiterCallEntity toEntity(WaiterCall domain) {
    return new WaiterCallEntity(
        domain.getId(),
        domain.getRestaurantId(),
        domain.getTableCod(),
        domain.getCreatedAt(),
        domain.getDismissedAt(),
        domain.getCallCount());
  }

  private WaiterCall toDomain(WaiterCallEntity entity) {
    return new WaiterCall(
        entity.getId(),
        entity.getRestaurantId(),
        entity.getTableCod(),
        entity.getCreatedAt(),
        entity.getDismissedAt(),
        entity.getCallCount());
  }
}

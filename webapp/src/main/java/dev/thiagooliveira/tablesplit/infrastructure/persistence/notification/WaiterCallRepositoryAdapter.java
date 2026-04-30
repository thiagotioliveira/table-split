package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.application.notification.WaiterCallRepository;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class WaiterCallRepositoryAdapter implements WaiterCallRepository {

  private final WaiterCallJpaRepository jpaRepository;
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant
          .RestaurantJpaRepository
      restaurantJpaRepository;
  private final ApplicationEventPublisher eventPublisher;

  public WaiterCallRepositoryAdapter(
      WaiterCallJpaRepository jpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository
          restaurantJpaRepository,
      ApplicationEventPublisher eventPublisher) {
    this.jpaRepository = jpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void save(WaiterCall waiterCall) {
    WaiterCallEntity entity = toEntity(waiterCall);
    jpaRepository.save(entity);

    // Ensure accountId is populated for events
    if (waiterCall.getAccountId() == null) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              waiterCall.getRestaurantId());
      if (cachedAccountId == null) {
        cachedAccountId =
            this.restaurantJpaRepository
                .findById(waiterCall.getRestaurantId())
                .map(r -> r.getAccountId())
                .orElse(null);
      }
      waiterCall.setAccountId(cachedAccountId);
    }

    waiterCall.getDomainEvents().forEach(eventPublisher::publishEvent);
    waiterCall.clearEvents();
  }

  @Override
  public List<WaiterCall> findAllActiveByRestaurantId(UUID restaurantId) {
    return jpaRepository
        .findAllByRestaurantIdAndDismissedAtIsNullOrderByCreatedAtDesc(restaurantId)
        .stream()
        .map(this::toDomainWithAccount)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<WaiterCall> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomainWithAccount);
  }

  @Override
  public Optional<WaiterCall> findActiveByRestaurantIdAndTableCod(
      UUID restaurantId, String tableCod) {
    return jpaRepository
        .findByRestaurantIdAndTableCodAndDismissedAtIsNull(restaurantId, tableCod)
        .map(this::toDomainWithAccount);
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

  private WaiterCall toDomainWithAccount(WaiterCallEntity entity) {
    WaiterCall domain =
        new WaiterCall(
            entity.getId(),
            entity.getRestaurantId(),
            entity.getTableCod(),
            entity.getCreatedAt(),
            entity.getDismissedAt(),
            entity.getCallCount());

    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            domain.getRestaurantId());
    if (cachedAccountId != null) {
      domain.setAccountId(cachedAccountId);
    } else {
      this.restaurantJpaRepository
          .findById(domain.getRestaurantId())
          .ifPresent(r -> domain.setAccountId(r.getAccountId()));
    }

    return domain;
  }
}

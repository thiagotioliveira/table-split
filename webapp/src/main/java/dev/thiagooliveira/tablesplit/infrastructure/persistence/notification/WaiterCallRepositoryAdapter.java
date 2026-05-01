package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCallRepository;
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
  private final WaiterCallEntityMapper mapper;

  public WaiterCallRepositoryAdapter(
      WaiterCallJpaRepository jpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository
          restaurantJpaRepository,
      ApplicationEventPublisher eventPublisher,
      WaiterCallEntityMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
  }

  @Override
  public void save(WaiterCall waiterCall) {
    jpaRepository.save(mapper.toEntity(waiterCall));

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

  private WaiterCall toDomainWithAccount(WaiterCallEntity entity) {
    WaiterCall domain = mapper.toDomain(entity);
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

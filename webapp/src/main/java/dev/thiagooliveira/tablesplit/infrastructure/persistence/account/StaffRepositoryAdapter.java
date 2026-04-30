package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class StaffRepositoryAdapter implements StaffRepository {

  private final StaffJpaRepository staffJpaRepository;
  private final RestaurantJpaRepository restaurantJpaRepository;
  private final ApplicationEventPublisher eventPublisher;

  public StaffRepositoryAdapter(
      StaffJpaRepository staffJpaRepository,
      RestaurantJpaRepository restaurantJpaRepository,
      ApplicationEventPublisher eventPublisher) {
    this.staffJpaRepository = staffJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void save(Staff staff) {
    this.staffJpaRepository.save(StaffEntity.fromDomain(staff));

    // Ensure accountId is populated for events
    if (staff.getAccountId() == null) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              staff.getRestaurantId());
      if (cachedAccountId == null) {
        cachedAccountId =
            this.restaurantJpaRepository
                .findById(staff.getRestaurantId())
                .map(r -> r.getAccountId())
                .orElse(null);
      }
      staff.setAccountId(cachedAccountId);
    }

    staff.getDomainEvents().forEach(eventPublisher::publishEvent);
    staff.clearEvents();
  }

  @Override
  public Optional<Staff> findById(UUID id) {
    return this.staffJpaRepository.findById(id).map(this::toDomainWithAccount);
  }

  private Staff toDomainWithAccount(StaffEntity entity) {
    Staff domain = entity.toDomain();
    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            domain.getRestaurantId());
    if (cachedAccountId != null) {
      domain.setAccountId(cachedAccountId);
    } else {
      // Fallback if entity doesn't have restaurant loaded
      this.restaurantJpaRepository
          .findById(domain.getRestaurantId())
          .ifPresent(r -> domain.setAccountId(r.getAccountId()));
    }
    return domain;
  }

  @Override
  public Optional<Staff> findByEmail(String email) {
    return this.staffJpaRepository.findByEmail(email).map(this::toDomainWithAccount);
  }

  @Override
  public List<Staff> findByRestaurantId(UUID restaurantId) {
    return this.staffJpaRepository.findByRestaurantId(restaurantId).stream()
        .map(this::toDomainWithAccount)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    this.staffJpaRepository.deleteById(id);
  }

  @Override
  public long count(UUID restaurantId) {
    return this.staffJpaRepository.countByRestaurantId(restaurantId);
  }
}

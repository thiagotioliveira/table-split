package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.application.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class StaffRepositoryAdapter implements StaffRepository {

  private final StaffJpaRepository staffJpaRepository;

  public StaffRepositoryAdapter(StaffJpaRepository staffJpaRepository) {
    this.staffJpaRepository = staffJpaRepository;
  }

  @Override
  public void save(Staff staff) {
    this.staffJpaRepository.save(StaffEntity.fromDomain(staff));
  }

  @Override
  public Optional<Staff> findById(UUID id) {
    return this.staffJpaRepository.findById(id).map(StaffEntity::toDomain);
  }

  @Override
  public Optional<Staff> findByEmail(String email) {
    return this.staffJpaRepository.findByEmail(email).map(StaffEntity::toDomain);
  }

  @Override
  public List<Staff> findByRestaurantId(UUID restaurantId) {
    return this.staffJpaRepository.findByRestaurantId(restaurantId).stream()
        .map(StaffEntity::toDomain)
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

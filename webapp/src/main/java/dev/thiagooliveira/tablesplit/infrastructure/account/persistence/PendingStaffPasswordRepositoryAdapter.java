package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import dev.thiagooliveira.tablesplit.domain.account.PendingStaffPassword;
import dev.thiagooliveira.tablesplit.domain.account.PendingStaffPasswordRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PendingStaffPasswordRepositoryAdapter implements PendingStaffPasswordRepository {

  private final PendingStaffPasswordJpaRepository jpaRepository;

  public PendingStaffPasswordRepositoryAdapter(PendingStaffPasswordJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  @Transactional
  public void save(PendingStaffPassword pendingStaffPassword) {
    PendingStaffPasswordEntity entity =
        jpaRepository
            .findById(pendingStaffPassword.getId())
            .orElseGet(PendingStaffPasswordEntity::new);

    entity.setId(pendingStaffPassword.getId());
    entity.setEmail(pendingStaffPassword.getEmail());
    entity.setExpiresAt(pendingStaffPassword.getExpiresAt());

    jpaRepository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PendingStaffPassword> findById(UUID id) {
    return jpaRepository
        .findById(id)
        .map(
            entity ->
                new PendingStaffPassword(entity.getId(), entity.getEmail(), entity.getExpiresAt()));
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }
}

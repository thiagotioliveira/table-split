package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import dev.thiagooliveira.tablesplit.domain.account.PendingUserPasswordReset;
import dev.thiagooliveira.tablesplit.domain.account.PendingUserPasswordResetRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PendingUserPasswordResetRepositoryAdapter
    implements PendingUserPasswordResetRepository {

  private final PendingUserPasswordResetJpaRepository jpaRepository;

  public PendingUserPasswordResetRepositoryAdapter(
      PendingUserPasswordResetJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  @Transactional
  public void save(PendingUserPasswordReset pendingReset) {
    PendingUserPasswordResetEntity entity =
        jpaRepository.findById(pendingReset.getId()).orElseGet(PendingUserPasswordResetEntity::new);

    entity.setId(pendingReset.getId());
    entity.setEmail(pendingReset.getEmail());
    entity.setExpiresAt(pendingReset.getExpiresAt());

    jpaRepository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PendingUserPasswordReset> findById(UUID id) {
    return jpaRepository
        .findById(id)
        .map(
            entity ->
                new PendingUserPasswordReset(
                    entity.getId(), entity.getEmail(), entity.getExpiresAt()));
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }
}

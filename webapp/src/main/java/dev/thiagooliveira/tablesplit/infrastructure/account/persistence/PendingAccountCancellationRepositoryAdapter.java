package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellation;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellationRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PendingAccountCancellationRepositoryAdapter
    implements PendingAccountCancellationRepository {

  private final PendingAccountCancellationJpaRepository jpaRepository;

  public PendingAccountCancellationRepositoryAdapter(
      PendingAccountCancellationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  @Transactional
  public void save(PendingAccountCancellation pendingAccountCancellation) {
    PendingAccountCancellationEntity entity =
        jpaRepository
            .findByAccountId(pendingAccountCancellation.getAccountId())
            .orElseGet(PendingAccountCancellationEntity::new);

    entity.setId(pendingAccountCancellation.getId());
    entity.setAccountId(pendingAccountCancellation.getAccountId());
    entity.setCode(pendingAccountCancellation.getCode());
    entity.setExpiresAt(pendingAccountCancellation.getExpiresAt());

    jpaRepository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PendingAccountCancellation> findByAccountId(UUID accountId) {
    return jpaRepository
        .findByAccountId(accountId)
        .map(
            entity ->
                new PendingAccountCancellation(
                    entity.getId(),
                    entity.getAccountId(),
                    entity.getCode(),
                    entity.getExpiresAt()));
  }

  @Override
  @Transactional
  public void deleteByAccountId(UUID accountId) {
    jpaRepository.deleteByAccountId(accountId);
  }
}

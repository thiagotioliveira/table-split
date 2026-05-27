package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingAccountCancellationJpaRepository
    extends JpaRepository<PendingAccountCancellationEntity, UUID> {
  Optional<PendingAccountCancellationEntity> findByAccountId(UUID accountId);

  void deleteByAccountId(UUID accountId);
}

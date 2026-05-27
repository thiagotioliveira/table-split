package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface PendingAccountCancellationRepository {
  void save(PendingAccountCancellation pendingAccountCancellation);

  Optional<PendingAccountCancellation> findByAccountId(UUID accountId);

  void deleteByAccountId(UUID accountId);
}

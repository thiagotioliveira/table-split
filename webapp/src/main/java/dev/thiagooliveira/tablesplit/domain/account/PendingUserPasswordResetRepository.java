package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface PendingUserPasswordResetRepository {

  void save(PendingUserPasswordReset pendingReset);

  Optional<PendingUserPasswordReset> findById(UUID id);

  void deleteById(UUID id);
}

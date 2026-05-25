package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface PendingStaffPasswordRepository {
  void save(PendingStaffPassword pendingStaffPassword);

  Optional<PendingStaffPassword> findById(UUID id);

  void deleteById(UUID id);
}

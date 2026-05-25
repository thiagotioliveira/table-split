package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Optional;

public interface PendingRegistrationRepository {
  void save(PendingRegistration pendingRegistration);

  Optional<PendingRegistration> findByEmail(String email);

  void deleteByEmail(String email);
}

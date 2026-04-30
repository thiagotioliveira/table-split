package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
  Optional<Account> findById(UUID accountId);

  void save(Account account);
}

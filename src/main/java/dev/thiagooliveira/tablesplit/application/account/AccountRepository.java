package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
  Optional<Account> findById(UUID accountId);

  void save(Account account);
}

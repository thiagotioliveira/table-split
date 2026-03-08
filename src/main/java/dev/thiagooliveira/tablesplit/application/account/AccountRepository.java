package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;

public interface AccountRepository {

  void save(Account account);
}

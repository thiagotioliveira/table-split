package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import java.util.Optional;
import java.util.UUID;

public class GetAccount {
  private final AccountRepository accountRepository;

  public GetAccount(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Optional<Account> execute(UUID accountId) {
    return accountRepository.findById(accountId);
  }
}

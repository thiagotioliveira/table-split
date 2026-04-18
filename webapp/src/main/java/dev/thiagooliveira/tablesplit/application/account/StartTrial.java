package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartTrial {

  private final AccountRepository accountRepository;

  public StartTrial(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Transactional
  public void execute(UUID accountId) {
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("error.account.not_found"));

    account.startTrial();
    accountRepository.save(account);
  }
}

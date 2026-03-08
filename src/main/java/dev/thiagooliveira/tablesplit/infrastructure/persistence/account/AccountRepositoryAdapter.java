package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.application.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountRepositoryAdapter implements AccountRepository {

  private final AccountJpaRepository accountJpaRepository;

  public AccountRepositoryAdapter(AccountJpaRepository accountJpaRepository) {
    this.accountJpaRepository = accountJpaRepository;
  }

  @Override
  public void save(Account account) {
    this.accountJpaRepository.save(AccountEntity.fromDomain(account));
  }
}

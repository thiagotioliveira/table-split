package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.application.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AccountRepositoryAdapter implements AccountRepository {

  private final AccountJpaRepository accountJpaRepository;

  public AccountRepositoryAdapter(AccountJpaRepository accountJpaRepository) {
    this.accountJpaRepository = accountJpaRepository;
  }

  @Override
  public Optional<Account> findById(UUID accountId) {
    return this.accountJpaRepository.findById(accountId).map(AccountEntity::toDomain);
  }

  @Override
  public void save(Account account) {
    this.accountJpaRepository.save(AccountEntity.fromDomain(account));
  }
}

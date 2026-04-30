package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AccountRepositoryAdapter implements AccountRepository {

  private final AccountJpaRepository accountJpaRepository;
  private final ApplicationEventPublisher eventPublisher;

  public AccountRepositoryAdapter(
      AccountJpaRepository accountJpaRepository, ApplicationEventPublisher eventPublisher) {
    this.accountJpaRepository = accountJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<Account> findById(UUID accountId) {
    return this.accountJpaRepository.findById(accountId).map(AccountEntity::toDomain);
  }

  @Override
  public void save(Account account) {
    this.accountJpaRepository.save(AccountEntity.fromDomain(account));
    account.getDomainEvents().forEach(eventPublisher::publishEvent);
    account.clearEvents();
  }
}

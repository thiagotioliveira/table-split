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
  private final AccountEntityMapper mapper;

  public AccountRepositoryAdapter(
      AccountJpaRepository accountJpaRepository,
      ApplicationEventPublisher eventPublisher,
      AccountEntityMapper mapper) {
    this.accountJpaRepository = accountJpaRepository;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
  }

  @Override
  public Optional<Account> findById(UUID accountId) {
    return this.accountJpaRepository.findById(accountId).map(mapper::toDomain);
  }

  @Override
  public void save(Account account) {
    this.accountJpaRepository.save(mapper.toEntity(account));
    account.getDomainEvents().forEach(eventPublisher::publishEvent);
    account.clearEvents();
  }
}

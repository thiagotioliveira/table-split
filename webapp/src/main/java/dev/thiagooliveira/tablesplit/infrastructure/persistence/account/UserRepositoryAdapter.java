package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final ApplicationEventPublisher eventPublisher;

  public UserRepositoryAdapter(
      UserJpaRepository userJpaRepository, ApplicationEventPublisher eventPublisher) {
    this.userJpaRepository = userJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void save(User user) {
    this.userJpaRepository.save(UserEntity.fromDomain(user));
    user.getDomainEvents().forEach(eventPublisher::publishEvent);
    user.clearEvents();
  }

  @Override
  public Optional<User> findById(UUID userId) {
    return this.userJpaRepository.findById(userId).map(UserEntity::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return this.userJpaRepository.findByEmail(email).map(UserEntity::toDomain);
  }

  @Override
  public List<User> findByAccountId(UUID accountId) {
    return this.userJpaRepository.findByAccountId(accountId).stream()
        .map(UserEntity::toDomain)
        .toList();
  }
}

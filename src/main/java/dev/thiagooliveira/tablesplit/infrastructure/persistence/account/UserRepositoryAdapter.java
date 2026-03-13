package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.application.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.account.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public void save(User user) {
    this.userJpaRepository.save(UserEntity.fromDomain(user));
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

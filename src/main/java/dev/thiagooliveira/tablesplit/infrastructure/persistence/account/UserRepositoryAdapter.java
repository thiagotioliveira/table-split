package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.application.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserPassword;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public void save(UserPassword userPassword) {
    this.userJpaRepository.save(UserEntity.fromDomain(userPassword));
  }

  @Override
  public void save(User user) {
    this.userJpaRepository.save(UserEntity.fromDomain(user));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return this.userJpaRepository.findByEmail(email).map(UserEntity::toDomain);
  }
}

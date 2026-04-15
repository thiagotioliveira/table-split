package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetUser {

  private final UserRepository userRepository;

  public GetUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> execute(UUID accountId) {
    return this.userRepository.findByAccountId(accountId);
  }

  public Optional<User> execute(UUID accountId, UUID userId) {
    return this.userRepository.findById(userId);
  }
}

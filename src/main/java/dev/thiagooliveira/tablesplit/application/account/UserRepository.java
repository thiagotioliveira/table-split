package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void save(User user);

  Optional<User> findById(UUID userId);

  Optional<User> findByEmail(String email);

  List<User> findByAccountId(UUID accountId);
}

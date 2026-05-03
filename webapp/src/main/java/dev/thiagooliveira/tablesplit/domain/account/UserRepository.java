package dev.thiagooliveira.tablesplit.domain.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void save(User user);

  Optional<User> findById(UUID userId);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhone(String phone);

  List<User> findByAccountId(UUID accountId);
}

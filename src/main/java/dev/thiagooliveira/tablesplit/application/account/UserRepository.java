package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserPassword;
import java.util.Optional;

public interface UserRepository {

  void save(UserPassword userPassword);

  void save(User user);

  Optional<User> findByEmail(String email);
}

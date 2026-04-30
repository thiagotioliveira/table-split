package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateUserCommand;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import java.util.UUID;

public class UpdateUser {

  private final UserRepository userRepository;

  public UpdateUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void execute(UUID userId, UpdateUserCommand command) {
    var user = this.userRepository.findById(userId).orElseThrow();
    user.update(
        command.firstName(),
        command.lastName(),
        command.email(),
        user.getPhone(),
        command.language());
    this.userRepository.save(user);
  }
}

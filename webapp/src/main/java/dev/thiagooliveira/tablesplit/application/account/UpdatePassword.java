package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdatePasswordCommand;
import java.util.UUID;

public class UpdatePassword {

  private final UserRepository userRepository;

  public UpdatePassword(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void execute(UUID userId, UpdatePasswordCommand command) {
    var user = this.userRepository.findById(userId).orElseThrow();
    user.updatePassword(command.newPassword());
    this.userRepository.save(user);
  }
}

package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdatePasswordCommand;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdatePassword {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UpdatePassword(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public void execute(UUID userId, UpdatePasswordCommand command) {
    var user = this.userRepository.findById(userId).orElseThrow();
    user.updatePassword(command.newPassword(), passwordEncoder);
    this.userRepository.save(user);
  }
}

package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.command.UpdatePasswordCommand;
import dev.thiagooliveira.tablesplit.domain.event.PasswordUpdatedEvent;
import java.util.UUID;

public class UpdatePassword {

  private final EventPublisher eventPublisher;
  private final UserRepository userRepository;

  public UpdatePassword(EventPublisher eventPublisher, UserRepository userRepository) {
    this.eventPublisher = eventPublisher;
    this.userRepository = userRepository;
  }

  public void execute(UUID userId, UpdatePasswordCommand command) {
    var user = this.userRepository.findById(userId).orElseThrow(); // TODO
    user.setPassword(command.newPassword());
    this.userRepository.save(user);
    this.eventPublisher.publishEvent(new PasswordUpdatedEvent(user.getAccountId(), user.getId()));
  }
}

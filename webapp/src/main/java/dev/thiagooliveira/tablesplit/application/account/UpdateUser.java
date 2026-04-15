package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.command.UpdateUserCommand;
import dev.thiagooliveira.tablesplit.domain.event.UserUpdatedEvent;
import java.util.UUID;

public class UpdateUser {

  private final EventPublisher eventPublisher;
  private final UserRepository userRepository;

  public UpdateUser(EventPublisher eventPublisher, UserRepository userRepository) {
    this.eventPublisher = eventPublisher;
    this.userRepository = userRepository;
  }

  public void execute(UUID userId, UpdateUserCommand command) {
    var user = this.userRepository.findById(userId).orElseThrow(); // TODO
    user.setFirstName(command.firstName());
    user.setLastName(command.lastName());
    user.setEmail(command.email());
    user.setLanguage(command.language());
    this.userRepository.save(user);

    this.eventPublisher.publishEvent(new UserUpdatedEvent(user.getAccountId(), user));
  }
}

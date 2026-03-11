package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserPassword;
import dev.thiagooliveira.tablesplit.domain.event.AccountCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.UserCreatedEvent;
import java.util.UUID;

public class CreateAccount {

  private final EventPublisher eventPublisher;
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public CreateAccount(
      EventPublisher eventPublisher,
      AccountRepository accountRepository,
      UserRepository userRepository) {
    this.eventPublisher = eventPublisher;
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  public User execute(CreateAccountCommand command) {
    var userCommand = command.user();
    if (this.userRepository.findByEmail(userCommand.email()).isPresent()) {
      throw new RuntimeException(); // TODO
    }

    var account = new Account();
    account.setId(UUID.randomUUID());
    this.accountRepository.save(account);

    var userPass = new UserPassword();
    userPass.setUser(new User());
    userPass.getUser().setId(UUID.randomUUID());
    userPass.getUser().setFirstName(userCommand.firstName());
    userPass.getUser().setLastName(userCommand.lastName());
    userPass.getUser().setEmail(userCommand.email());
    userPass.getUser().setPhone(userCommand.phone());
    userPass.getUser().setAccountId(account.getId());
    userPass.getUser().setLanguage(userCommand.language());
    userPass.setPassword(userCommand.password());
    this.userRepository.save(userPass);

    this.eventPublisher.publishEvent(new UserCreatedEvent(account.getId(), userPass.getUser()));

    this.eventPublisher.publishEvent(
        new AccountCreatedEvent(
            account.getId(),
            new AccountCreatedEvent.AccountCreatedEventDetails(
                command.restaurant().name(),
                command.restaurant().slug(),
                command.restaurant().description(),
                command.restaurant().phone(),
                command.restaurant().email(),
                command.restaurant().website(),
                command.restaurant().address(),
                command.restaurant().currency(),
                command.restaurant().serviceFee())));

    return userPass.getUser();
  }
}

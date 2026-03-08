package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserPassword;
import dev.thiagooliveira.tablesplit.domain.event.AccountCreatedEvent;
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

  public Account execute(CreateAccountCommand command) {
    var userCommand = command.user();
    if (this.userRepository.findByEmail(userCommand.email()).isPresent()) {
      throw new RuntimeException(); // TODO
    }

    var account = new Account();
    account.setId(UUID.randomUUID());
    this.accountRepository.save(account);

    var newUser = new UserPassword();
    newUser.setUser(new User());
    newUser.getUser().setId(UUID.randomUUID());
    newUser.getUser().setFirstName(userCommand.firstName());
    newUser.getUser().setLastName(userCommand.lastName());
    newUser.getUser().setEmail(userCommand.email());
    newUser.getUser().setPhone(userCommand.phone());
    newUser.getUser().setAccountId(account.getId());
    newUser.setPassword(userCommand.password());
    this.userRepository.save(newUser);

    this.eventPublisher.publishEvent(
        new AccountCreatedEvent(
            account.getId(), new AccountCreatedEvent.RestaurantData(command.restaurant())));

    return account;
  }
}

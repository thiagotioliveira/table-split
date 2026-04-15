package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.UserAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.event.AccountCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.UserCreatedEvent;
import java.time.OffsetDateTime;
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

  public User execute(CreateAccountCommand command) throws UserAlreadyRegisteredException {
    var userCommand = command.user();
    if (this.userRepository.findByEmail(userCommand.email()).isPresent()) {
      throw new UserAlreadyRegisteredException();
    }

    var account = new Account();
    account.setId(UUID.randomUUID());
    account.setPlan(Plan.PRO);
    account.setCreatedAt(OffsetDateTime.now(command.zone()));
    account.setActive(true);
    this.accountRepository.save(account);

    var user = new User();
    user.setId(UUID.randomUUID());
    user.setFirstName(userCommand.firstName());
    user.setLastName(userCommand.lastName());
    user.setEmail(userCommand.email());
    user.setPhone(userCommand.phone());
    user.setAccountId(account.getId());
    user.setLanguage(userCommand.language());
    user.setPassword(userCommand.password());
    user.setRole(Role.RESTAURANT_ADMIN);
    this.userRepository.save(user);

    this.eventPublisher.publishEvent(new UserCreatedEvent(account.getId(), user));

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
                userCommand.language(),
                command.restaurant().currency(),
                command.restaurant().serviceFee(),
                command.restaurant().numberOfTables(),
                command.restaurant().cuisineType(),
                command.restaurant().averagePrice(),
                command.restaurant().tags())));

    return user;
  }
}

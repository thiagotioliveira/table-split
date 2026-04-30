package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.UserAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.event.AccountCreatedEvent;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateAccount {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public CreateAccount(AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  public User execute(CreateAccountCommand command) throws UserAlreadyRegisteredException {
    var userCommand = command.user();
    if (this.userRepository.findByEmail(userCommand.email()).isPresent()) {
      throw new UserAlreadyRegisteredException();
    }

    var account =
        Account.create(
            UUID.randomUUID(),
            command.plan(),
            OffsetDateTime.now(command.zone()),
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
                command.plan() == Plan.STARTER ? 0 : command.restaurant().numberOfTables(),
                command.restaurant().cuisineType(),
                command.restaurant().averagePrice(),
                command.restaurant().tags()));

    this.accountRepository.save(account);

    var user =
        User.create(
            UUID.randomUUID(),
            account.getId(),
            userCommand.firstName(),
            userCommand.lastName(),
            userCommand.email(),
            userCommand.phone(),
            userCommand.language(),
            userCommand.password(),
            Role.RESTAURANT_ADMIN);

    this.userRepository.save(user);

    return user;
  }
}

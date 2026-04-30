package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateUserCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.UserAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.AccountCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.UserCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateAccountTest {

  @Mock private AccountRepository accountRepository;

  @Mock private UserRepository userRepository;

  private CreateAccount createAccount;

  @BeforeEach
  void setUp() {
    createAccount = new CreateAccount(accountRepository, userRepository);
  }

  @Test
  void execute_shouldCreateAccountAndUserSuccessfully() {
    // Arrange
    CreateUserCommand userCommand =
        new CreateUserCommand(
            "Thiago", "Oliveira", "thiago@example.com", "123456789", "password", Language.PT);
    CreateRestaurantCommand restaurantCommand =
        new CreateRestaurantCommand(
            "Restaurante Teste",
            "restaurante-teste",
            "Desc",
            "987654321",
            "contato@restaurante.com",
            "www.restaurante.com",
            "Rua Teste",
            Currency.BRL,
            10,
            10,
            CuisineType.BRAZILIAN,
            AveragePrice.PRICE_20_50,
            List.of());
    CreateAccountCommand command =
        new CreateAccountCommand(
            userCommand, restaurantCommand, ZoneId.of("UTC"), Plan.PROFESSIONAL);

    when(userRepository.findByEmail(userCommand.email())).thenReturn(Optional.empty());

    // Act
    User result = createAccount.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(userCommand.email(), result.getEmail());
    assertEquals(Role.RESTAURANT_ADMIN, result.getRole());

    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository, times(1)).save(accountCaptor.capture());

    Account savedAccount = accountCaptor.getValue();
    assertFalse(savedAccount.getDomainEvents().isEmpty());
    assertTrue(
        savedAccount.getDomainEvents().stream().anyMatch(e -> e instanceof AccountCreatedEvent));

    verify(userRepository, times(1)).save(any(User.class));
    // User also has events
    assertFalse(result.getDomainEvents().isEmpty());
    assertTrue(result.getDomainEvents().stream().anyMatch(e -> e instanceof UserCreatedEvent));
  }

  @Test
  void execute_shouldThrowException_whenUserAlreadyRegistered() {
    // Arrange
    CreateUserCommand userCommand =
        new CreateUserCommand(
            "Thiago", "Oliveira", "thiago@example.com", "123456789", "password", Language.PT);
    CreateAccountCommand command =
        new CreateAccountCommand(userCommand, null, ZoneId.of("UTC"), Plan.PROFESSIONAL);

    when(userRepository.findByEmail(userCommand.email())).thenReturn(Optional.of(new User()));

    // Act & Assert
    assertThrows(UserAlreadyRegisteredException.class, () -> createAccount.execute(command));

    verify(accountRepository, never()).save(any());
    verify(userRepository, never()).save(any());
  }
}

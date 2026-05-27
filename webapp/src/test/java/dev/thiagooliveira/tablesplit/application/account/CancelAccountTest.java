package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.AccountStatus;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellation;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellationRepository;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelAccountTest {

  @Mock private AccountRepository accountRepository;

  @Mock private PendingAccountCancellationRepository pendingAccountCancellationRepository;

  @Mock private UserRepository userRepository;

  @Mock private RestaurantRepository restaurantRepository;

  @Mock private StaffRepository staffRepository;

  @Mock private UserSessionInvalidator sessionInvalidator;

  private CancelAccount cancelAccount;

  @BeforeEach
  void setUp() {
    cancelAccount =
        new CancelAccount(
            accountRepository,
            pendingAccountCancellationRepository,
            userRepository,
            restaurantRepository,
            staffRepository,
            sessionInvalidator);
  }

  @Test
  void execute_shouldCancelAccountSuccessfully() {
    // Arrange
    UUID accountId = UUID.randomUUID();
    String code = "123456";

    PendingAccountCancellation cancellation =
        new PendingAccountCancellation(
            UUID.randomUUID(), accountId, code, LocalDateTime.now().plusMinutes(15));

    Account account = new Account();
    account.setId(accountId);
    account.setStatus(AccountStatus.ACTIVE);
    account.setCreatedAt(OffsetDateTime.now());

    User adminUser = new User();
    adminUser.setEmail("admin@test.com");
    adminUser.setFirstName("Thiago");
    adminUser.setLanguage(Language.PT);
    adminUser.setRole(Role.RESTAURANT_ADMIN);

    Restaurant restaurant = new Restaurant();
    restaurant.setId(UUID.randomUUID());
    restaurant.setName("Pasta Place");

    Staff staff = new Staff();
    staff.setEmail("staff@test.com");

    when(pendingAccountCancellationRepository.findByAccountId(accountId))
        .thenReturn(Optional.of(cancellation));
    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(userRepository.findByAccountId(accountId)).thenReturn(List.of(adminUser));
    when(restaurantRepository.findByAccountId(accountId)).thenReturn(Optional.of(restaurant));
    when(staffRepository.findByRestaurantId(restaurant.getId())).thenReturn(List.of(staff));

    // Act
    CancelAccount.CancellationResult result = cancelAccount.execute(accountId, code);

    // Assert
    assertNotNull(result);
    assertEquals("admin@test.com", result.email());
    assertEquals("Thiago", result.firstName());
    assertEquals("PT", result.language());
    assertEquals("Pasta Place", result.restaurantName());

    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository, times(1)).save(accountCaptor.capture());
    Account savedAccount = accountCaptor.getValue();
    assertEquals(AccountStatus.CANCELLED, savedAccount.getStatus());
    assertNotNull(savedAccount.getCancelledAt());

    verify(pendingAccountCancellationRepository, times(1)).deleteByAccountId(accountId);
    verify(sessionInvalidator, times(1))
        .invalidateSessionsForEmails(List.of("admin@test.com", "staff@test.com"));
  }

  @Test
  void execute_shouldThrowException_whenCancellationRequestNotFound() {
    // Arrange
    UUID accountId = UUID.randomUUID();
    when(pendingAccountCancellationRepository.findByAccountId(accountId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> cancelAccount.execute(accountId, "123456"));

    verify(accountRepository, never()).save(any());
    verify(sessionInvalidator, never()).invalidateSessionsForEmails(any());
  }

  @Test
  void execute_shouldThrowException_whenCodeExpired() {
    // Arrange
    UUID accountId = UUID.randomUUID();
    String code = "123456";

    PendingAccountCancellation cancellation =
        new PendingAccountCancellation(
            UUID.randomUUID(), accountId, code, LocalDateTime.now().minusMinutes(5));

    when(pendingAccountCancellationRepository.findByAccountId(accountId))
        .thenReturn(Optional.of(cancellation));

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> cancelAccount.execute(accountId, code));

    verify(accountRepository, never()).save(any());
    verify(sessionInvalidator, never()).invalidateSessionsForEmails(any());
  }

  @Test
  void execute_shouldThrowException_whenCodeIncorrect() {
    // Arrange
    UUID accountId = UUID.randomUUID();
    String code = "123456";

    PendingAccountCancellation cancellation =
        new PendingAccountCancellation(
            UUID.randomUUID(), accountId, code, LocalDateTime.now().plusMinutes(15));

    when(pendingAccountCancellationRepository.findByAccountId(accountId))
        .thenReturn(Optional.of(cancellation));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> cancelAccount.execute(accountId, "999999"));

    verify(accountRepository, never()).save(any());
    verify(sessionInvalidator, never()).invalidateSessionsForEmails(any());
  }
}

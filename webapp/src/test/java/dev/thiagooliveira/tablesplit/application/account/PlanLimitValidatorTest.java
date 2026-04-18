package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanLimitValidatorTest {

  @Mock private AccountRepository accountRepository;
  @Mock private RestaurantRepository restaurantRepository;

  private PlanLimitValidator planLimitValidator;

  @BeforeEach
  void setUp() {
    planLimitValidator = new PlanLimitValidator(accountRepository, restaurantRepository);
  }

  @Test
  void shouldThrowException_whenLimitReached() {
    UUID accountId = UUID.randomUUID();
    Account account = new Account();
    account.setPlan(Plan.STARTER); // Categories limit: 10

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

    assertThrows(
        PlanLimitExceededException.class,
        () -> planLimitValidator.validate(accountId, PlanLimitType.CATEGORIES, 10));
  }

  @Test
  void shouldNotThrowException_whenUnderLimit() {
    UUID accountId = UUID.randomUUID();
    Account account = new Account();
    account.setPlan(Plan.STARTER);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

    assertDoesNotThrow(() -> planLimitValidator.validate(accountId, PlanLimitType.CATEGORIES, 5));
  }

  @Test
  void shouldNotThrowException_whenUnlimited() {
    UUID accountId = UUID.randomUUID();
    Account account = new Account();
    account.setPlan(Plan.ENTERPRISE); // Unlimited categories (-1)

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

    assertDoesNotThrow(
        () -> planLimitValidator.validate(accountId, PlanLimitType.CATEGORIES, 9999));
  }

  @Test
  void shouldResolveAccountFromRestaurantId() {
    UUID restaurantId = UUID.randomUUID();
    UUID accountId = UUID.randomUUID();
    Restaurant restaurant = new Restaurant();
    restaurant.setAccountId(accountId);

    Account account = new Account();
    account.setPlan(Plan.STARTER);

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

    assertDoesNotThrow(
        () -> planLimitValidator.validateByRestaurantId(restaurantId, PlanLimitType.CATEGORIES, 5));
  }
}

package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.UUID;

public class PlanLimitValidator {

  private final AccountRepository accountRepository;
  private final RestaurantRepository restaurantRepository;

  public PlanLimitValidator(
      AccountRepository accountRepository, RestaurantRepository restaurantRepository) {
    this.accountRepository = accountRepository;
    this.restaurantRepository = restaurantRepository;
  }

  /**
   * Validates a limit for a specific account.
   *
   * @param accountId The ID of the account.
   * @param type The type of limit to validate.
   * @param currentCount The current count of the resource.
   * @throws PlanLimitExceededException if the limit is reached.
   */
  public void validate(UUID accountId, PlanLimitType type, long currentCount) {
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("error.account.not_found"));

    var plan = account.getEffectivePlan();
    var limits = plan.getLimits();
    int limit = type.getLimit(limits);

    if (!limits.isUnlimited(limit) && currentCount >= limit) {
      throw new PlanLimitExceededException(type.getErrorKey());
    }
  }

  /**
   * Validates a limit resolver the account from a restaurant ID.
   *
   * @param restaurantId The ID of the restaurant.
   * @param type The type of limit to validate.
   * @param currentCount The current count of the resource.
   * @throws PlanLimitExceededException if the limit is reached.
   */
  public void validateByRestaurantId(UUID restaurantId, PlanLimitType type, long currentCount) {
    var restaurant =
        restaurantRepository
            .findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("error.restaurant.not_found"));

    validate(restaurant.getAccountId(), type, currentCount);
  }
}

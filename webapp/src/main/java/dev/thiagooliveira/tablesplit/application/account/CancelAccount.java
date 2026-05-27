package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellation;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellationRepository;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CancelAccount {

  public record CancellationResult(
      String email, String firstName, String language, String restaurantName) {}

  private final AccountRepository accountRepository;
  private final PendingAccountCancellationRepository pendingAccountCancellationRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;
  private final StaffRepository staffRepository;
  private final UserSessionInvalidator sessionInvalidator;

  public CancelAccount(
      AccountRepository accountRepository,
      PendingAccountCancellationRepository pendingAccountCancellationRepository,
      UserRepository userRepository,
      RestaurantRepository restaurantRepository,
      StaffRepository staffRepository,
      UserSessionInvalidator sessionInvalidator) {
    this.accountRepository = accountRepository;
    this.pendingAccountCancellationRepository = pendingAccountCancellationRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
    this.staffRepository = staffRepository;
    this.sessionInvalidator = sessionInvalidator;
  }

  public CancellationResult execute(UUID accountId, String code) {
    PendingAccountCancellation cancellation =
        pendingAccountCancellationRepository
            .findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Cancellation request not found"));

    if (cancellation.isExpired()) {
      throw new IllegalStateException("Verification code expired");
    }

    if (!cancellation.getCode().equals(code.trim())) {
      throw new IllegalArgumentException("Incorrect verification code");
    }

    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    account.cancel();
    accountRepository.save(account);
    pendingAccountCancellationRepository.deleteByAccountId(accountId);

    // Collect affected usernames (emails)
    List<String> affectedEmails = new ArrayList<>();

    List<User> users = userRepository.findByAccountId(accountId);
    for (User user : users) {
      if (user.getEmail() != null) {
        affectedEmails.add(user.getEmail().trim().toLowerCase());
      }
    }

    User adminUser =
        users.stream()
            .filter(u -> u.getRole() == Role.RESTAURANT_ADMIN)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));

    var restaurantName =
        restaurantRepository.findByAccountId(accountId).map(Restaurant::getName).orElse("");

    restaurantRepository
        .findByAccountId(accountId)
        .ifPresent(
            restaurant -> {
              List<Staff> staffMembers = staffRepository.findByRestaurantId(restaurant.getId());
              for (Staff staff : staffMembers) {
                if (staff.getEmail() != null) {
                  affectedEmails.add(staff.getEmail().trim().toLowerCase());
                }
              }
            });

    // Invalidate active sessions via interface
    sessionInvalidator.invalidateSessionsForEmails(affectedEmails);

    return new CancellationResult(
        adminUser.getEmail().trim().toLowerCase(),
        adminUser.getFirstName(),
        adminUser.getLanguage().name(),
        restaurantName);
  }
}

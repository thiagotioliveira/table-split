package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellation;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellationRepository;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.infrastructure.account.event.PendingAccountCancellationCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.utils.VerificationCodeGenerator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;

public class RequestAccountCancellation {

  private final UserRepository userRepository;
  private final PendingAccountCancellationRepository pendingAccountCancellationRepository;
  private final ApplicationEventPublisher eventPublisher;

  public RequestAccountCancellation(
      UserRepository userRepository,
      PendingAccountCancellationRepository pendingAccountCancellationRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userRepository = userRepository;
    this.pendingAccountCancellationRepository = pendingAccountCancellationRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID accountId, String restaurantName, String baseUrl) {
    List<User> users = userRepository.findByAccountId(accountId);
    User adminUser =
        users.stream()
            .filter(u -> u.getRole() == Role.RESTAURANT_ADMIN)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));

    String code = VerificationCodeGenerator.generate6DigitCode();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

    PendingAccountCancellation cancellation =
        new PendingAccountCancellation(UUID.randomUUID(), accountId, code, expiresAt);
    pendingAccountCancellationRepository.save(cancellation);

    eventPublisher.publishEvent(
        new PendingAccountCancellationCreatedEvent(
            adminUser.getEmail().trim().toLowerCase(),
            code,
            adminUser.getFirstName(),
            adminUser.getLanguage().name(),
            baseUrl,
            restaurantName));
  }
}

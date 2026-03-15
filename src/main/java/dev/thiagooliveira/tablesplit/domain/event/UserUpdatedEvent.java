package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.account.User;
import java.util.UUID;

public class UserUpdatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID userId;

  public UserUpdatedEvent(UUID accountId, User user) {
    this.accountId = accountId;
    this.userId = user.getId();
  }

  public UUID getUserId() {
    return userId;
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }
}

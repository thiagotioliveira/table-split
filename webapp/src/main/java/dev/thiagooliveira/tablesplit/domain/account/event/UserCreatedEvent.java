package dev.thiagooliveira.tablesplit.domain.account.event;

import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import java.util.UUID;

public class UserCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID userId;

  public UserCreatedEvent(UUID accountId, User user) {
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

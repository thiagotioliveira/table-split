package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class PasswordUpdatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID userId;

  public PasswordUpdatedEvent(UUID accountId, UUID userId) {
    this.accountId = accountId;
    this.userId = userId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public UUID getUserId() {
    return userId;
  }
}

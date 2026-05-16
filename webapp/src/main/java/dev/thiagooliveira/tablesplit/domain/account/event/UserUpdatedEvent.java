package dev.thiagooliveira.tablesplit.domain.account.event;

import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import java.util.UUID;

public class UserUpdatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID userId;
  private final dev.thiagooliveira.tablesplit.domain.common.Language language;

  public UserUpdatedEvent(UUID accountId, User user) {
    this.accountId = accountId;
    this.userId = user.getId();
    this.language = user.getLanguage();
  }

  public UUID getUserId() {
    return userId;
  }

  public dev.thiagooliveira.tablesplit.domain.common.Language getLanguage() {
    return language;
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }
}

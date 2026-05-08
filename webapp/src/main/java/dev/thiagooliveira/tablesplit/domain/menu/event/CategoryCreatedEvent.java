package dev.thiagooliveira.tablesplit.domain.menu.event;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import java.util.UUID;

public class CategoryCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID categoryId;

  public CategoryCreatedEvent(UUID accountId, UUID categoryId) {
    this.accountId = accountId;
    this.categoryId = categoryId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }
}

package dev.thiagooliveira.tablesplit.domain.menu.event;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import java.util.UUID;

public class CategoryUpdatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID categoryId;

  public CategoryUpdatedEvent(UUID accountId, UUID categoryId) {
    this.accountId = accountId;
    this.categoryId = categoryId;
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }
}

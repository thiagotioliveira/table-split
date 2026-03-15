package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class CategoryDeletedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID categoryId;

  public CategoryDeletedEvent(UUID accountId, UUID categoryId) {
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

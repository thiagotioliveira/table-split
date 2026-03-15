package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class ItemUpdatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID itemId;

  public ItemUpdatedEvent(UUID accountId, UUID itemId) {
    this.accountId = accountId;
    this.itemId = itemId;
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  public UUID getItemId() {
    return itemId;
  }
}

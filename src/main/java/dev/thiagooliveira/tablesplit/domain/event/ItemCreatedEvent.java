package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class ItemCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID itemId;

  public ItemCreatedEvent(UUID accountId, UUID itemId) {
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

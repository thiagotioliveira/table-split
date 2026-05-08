package dev.thiagooliveira.tablesplit.domain.menu.event;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import java.util.UUID;

public class ItemDeletedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID itemId;

  public ItemDeletedEvent(UUID accountId, UUID itemId) {
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

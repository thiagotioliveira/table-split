package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class ItemDeletedEvent implements DomainEvent<ItemDeletedEvent.ItemDeletedEventDetails> {
  private final UUID accountId;
  private final UUID itemId;
  private final ItemDeletedEventDetails details;

  public ItemDeletedEvent(
      UUID accountId, UUID itemId, long total, long totalActive, long totalInactive) {
    this.accountId = accountId;
    this.itemId = itemId;
    this.details = new ItemDeletedEventDetails(total, totalActive, totalInactive);
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  @Override
  public ItemDeletedEventDetails getDetails() {
    return this.details;
  }

  public UUID getItemId() {
    return itemId;
  }

  public static class ItemDeletedEventDetails {
    private final long total;
    private final long totalActive;
    private final long totalInactive;

    public ItemDeletedEventDetails(long total, long totalActive, long totalInactive) {
      this.total = total;
      this.totalActive = totalActive;
      this.totalInactive = totalInactive;
    }

    public long getTotal() {
      return total;
    }

    public long getTotalActive() {
      return totalActive;
    }

    public long getTotalInactive() {
      return totalInactive;
    }
  }
}

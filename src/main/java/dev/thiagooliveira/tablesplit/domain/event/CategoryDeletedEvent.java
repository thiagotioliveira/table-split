package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class CategoryDeletedEvent
    implements DomainEvent<CategoryDeletedEvent.CategoryDeletedEventDetails> {
  private final UUID accountId;
  private final UUID categoryId;
  private final CategoryDeletedEventDetails details;

  public CategoryDeletedEvent(
      UUID accountId, UUID categoryId, long total, long totalActive, long totalInactive) {
    this.accountId = accountId;
    this.categoryId = categoryId;
    this.details = new CategoryDeletedEventDetails(total, totalActive, totalInactive);
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  @Override
  public CategoryDeletedEventDetails getDetails() {
    return this.details;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public static class CategoryDeletedEventDetails {
    private final long total;
    private final long totalActive;
    private final long totalInactive;

    public CategoryDeletedEventDetails(long total, long totalActive, long totalInactive) {
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

package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class CategoryCreatedEvent
    implements DomainEvent<CategoryCreatedEvent.CategoryCreatedEventDetails> {
  private final UUID accountId;
  private final UUID categoryId;
  private final CategoryCreatedEventDetails details;

  public CategoryCreatedEvent(
      UUID accountId, UUID categoryId, long total, long totalActive, long totalInactive) {
    this.accountId = accountId;
    this.categoryId = categoryId;
    this.details = new CategoryCreatedEventDetails(total, totalActive, totalInactive);
  }

  @Override
  public CategoryCreatedEventDetails getDetails() {
    return this.details;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public static class CategoryCreatedEventDetails {
    private final long total;
    private final long totalActive;
    private final long totalInactive;

    public CategoryCreatedEventDetails(long total, long totalActive, long totalInactive) {
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

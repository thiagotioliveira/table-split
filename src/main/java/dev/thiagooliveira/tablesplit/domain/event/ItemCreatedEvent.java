package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ItemCreatedEvent implements DomainEvent<ItemCreatedEvent.ItemCreatedEventDetails> {
  private final UUID accountId;
  private final UUID itemId;
  private final ItemCreatedEventDetails details;

  public ItemCreatedEvent(
      UUID accountId, Item item, long total, long totalActive, long totalInactive) {
    this.accountId = accountId;
    this.itemId = item.getId();
    this.details = new ItemCreatedEventDetails(item, total, totalActive, totalInactive);
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  @Override
  public ItemCreatedEventDetails getDetails() {
    return this.details;
  }

  public UUID getItemId() {
    return itemId;
  }

  public static class ItemCreatedEventDetails {
    private final UUID categoryId;
    private final Map<Language, String> categoryName;
    private final Map<Language, String> name;
    private final String imageUrl;
    private final BigDecimal price;
    private final long total;
    private final long totalActive;
    private final long totalInactive;

    public ItemCreatedEventDetails(Item item, long total, long totalActive, long totalInactive) {
      this.categoryId = item.getCategory().getId();
      this.categoryName = item.getCategory().getName();
      this.name = item.getName();
      this.price = item.getPrice();
      this.imageUrl = item.getImages().isEmpty() ? "" : item.getImages().getFirst().getName();
      this.total = total;
      this.totalActive = totalActive;
      this.totalInactive = totalInactive;
    }

    public UUID getCategoryId() {
      return categoryId;
    }

    public Map<Language, String> getCategoryName() {
      return categoryName;
    }

    public Map<Language, String> getName() {
      return name;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public String getImageUrl() {
      return imageUrl;
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

package dev.thiagooliveira.tablesplit.domain.dashboard.v1;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemAttributes {
  private final List<Item> list;
  private final long total;
  private final long totalActive;
  private final long totalInactive;

  public ItemAttributes() {
    this.list = new ArrayList<>();
    this.total = 0L;
    this.totalActive = 0L;
    this.totalInactive = 0L;
  }

  public ItemAttributes(List<Item> list, long total, long totalActive, long totalInactive) {
    this.list = new ArrayList<>(list);
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

  public List<Item> getList() {
    return list;
  }

  public void removeLast() {
    if (this.list.size() == 3) {
      this.list.removeLast();
    }
  }

  public static class Item {
    private final UUID id;
    private final UUID categoryId;
    private Map<Language, String> categoryName;
    private Map<Language, String> name;
    private String imageUrl;
    private BigDecimal price;

    public Item(
        UUID id,
        UUID categoryId,
        Map<Language, String> categoryName,
        Map<Language, String> name,
        String imageUrl,
        BigDecimal price) {
      this.id = id;
      this.categoryId = categoryId;
      this.categoryName = categoryName;
      this.name = name;
      this.imageUrl = imageUrl;
      this.price = price;
    }

    public UUID getId() {
      return id;
    }

    public UUID getCategoryId() {
      return categoryId;
    }

    public Map<Language, String> getCategoryName() {
      return categoryName;
    }

    public void setCategoryName(Map<Language, String> categoryName) {
      this.categoryName = categoryName;
    }

    public Map<Language, String> getName() {
      return name;
    }

    public void setName(Map<Language, String> name) {
      this.name = name;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(BigDecimal price) {
      this.price = price;
    }
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemModel {
  private final List<Item> list;
  private final long total;
  private final long totalActive;
  private final long totalInactive;

  public ItemModel(List<Item> list, long total, long totalActive, long totalInactive) {
    this.list = list;
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
    private Map<String, String> categoryName;
    private Map<String, String> name;
    private String imageUrl;
    private BigDecimal price;

    public Item(dev.thiagooliveira.tablesplit.domain.menu.Item item) {
      this.id = item.getId();
      this.categoryId = item.getCategory().getId();
      this.categoryName = convertMap(item.getCategory().getName());
      this.name = convertMap(item.getName());
      this.imageUrl = item.getImages().isEmpty() ? "" : item.getImages().getFirst().getName();
      this.price = item.getPrice();
    }

    private static Map<String, String> convertMap(Map<Language, String> map) {
      return map.entrySet().stream()
          .collect(
              Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
    }

    public UUID getId() {
      return id;
    }

    public UUID getCategoryId() {
      return categoryId;
    }

    public Map<String, String> getCategoryName() {
      return categoryName;
    }

    public void setCategoryName(Map<String, String> categoryName) {
      this.categoryName = categoryName;
    }

    public Map<String, String> getName() {
      return name;
    }

    public void setName(Map<String, String> name) {
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

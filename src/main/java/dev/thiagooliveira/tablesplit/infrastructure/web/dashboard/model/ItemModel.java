package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.ItemAttributes;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemModel {
  private final List<Item> list;
  private final long total;
  private final long totalActive;
  private final long totalInactive;

  public ItemModel(ItemAttributes attributes) {
    this.list = new ArrayList<>(attributes.getList().stream().map(Item::new).toList());
    this.total = attributes.getTotal();
    this.totalActive = attributes.getTotalActive();
    this.totalInactive = attributes.getTotalInactive();
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

    public Item(ItemAttributes.Item item) {
      this.id = item.getId();
      this.categoryId = item.getCategoryId();
      this.categoryName = convertMap(item.getCategoryName());
      this.name = convertMap(item.getName());
      this.imageUrl = item.getImageUrl();
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

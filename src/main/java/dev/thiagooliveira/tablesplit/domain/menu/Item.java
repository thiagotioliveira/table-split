package dev.thiagooliveira.tablesplit.domain.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Item {
  private UUID id;
  private UUID restaurantId;
  private UUID categoryId;
  private List<ItemImage> images;
  private Map<Language, String> name;
  private Map<Language, String> description;
  private BigDecimal price;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }

  public Map<Language, String> getDescription() {
    return description;
  }

  public void setDescription(Map<Language, String> description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public List<ItemImage> getImages() {
    return images;
  }

  public void setImages(List<ItemImage> images) {
    this.images = images;
  }
}

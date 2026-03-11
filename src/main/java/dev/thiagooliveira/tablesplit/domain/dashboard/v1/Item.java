package dev.thiagooliveira.tablesplit.domain.dashboard.v1;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class Item {
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

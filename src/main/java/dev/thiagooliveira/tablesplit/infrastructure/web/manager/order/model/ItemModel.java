package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ItemModel {
  private final String id;
  private final Map<String, String> name;
  private final BigDecimal price;
  private final String categoryId;

  public ItemModel(UUID id, Map<String, String> name, BigDecimal price, UUID categoryId) {
    this.id = id.toString();
    this.name = name;
    this.price = price;
    this.categoryId = categoryId.toString();
  }

  public String getId() {
    return id;
  }

  public Map<String, String> getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getCategoryId() {
    return categoryId;
  }
}

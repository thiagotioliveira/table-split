package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemModel {
  private final UUID id;
  private final UUID categoryId;
  private final Map<String, String> categoryName;
  private final Map<String, String> name;
  private final Map<String, String> description;
  private final BigDecimal price;
  private final String priceFormatted;

  public ItemModel(Item item, Map<String, String> categoryName, String symbol) {
    this.id = item.getId();
    this.categoryId = item.getCategoryId();
    this.categoryName = categoryName;
    this.name = convertMap(item.getName());
    this.description = convertMap(item.getDescription());
    this.price = item.getPrice();
    this.priceFormatted = String.format("%s %s", symbol, this.price);
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

  public Map<String, String> getName() {
    return name;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getPriceFormatted() {
    return priceFormatted;
  }
}

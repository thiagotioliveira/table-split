package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemModel {
  private final String id;
  private final String categoryId;
  private final List<ImageModel> images;
  private final List<String> imageUrls;
  private final Map<String, String> categoryName;
  private final Map<String, String> name;
  private final Map<String, String> description;
  private final BigDecimal price;
  private final String priceFormatted;

  public ItemModel(Item item, Map<String, String> categoryName, String symbol) {
    this.id = item.getId().toString();
    this.categoryId = item.getCategory().getId().toString();
    this.categoryName = categoryName;
    this.name = convertMap(item.getName());
    this.description = convertMap(item.getDescription());
    this.price = item.getPrice();
    this.priceFormatted = String.format("%s %s", symbol, this.price);
    this.images =
        item.getImages().stream().map(img -> new ImageModel(img.getId(), img.getName())).toList();
    this.imageUrls = this.images.stream().map(ImageModel::getUrl).toList();
  }

  private static Map<String, String> convertMap(Map<Language, String> map) {
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
  }

  public String getId() {
    return id;
  }

  public String getCategoryId() {
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

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public List<ImageModel> getImages() {
    return images;
  }
}

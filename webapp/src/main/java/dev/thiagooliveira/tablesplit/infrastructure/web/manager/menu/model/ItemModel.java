package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

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
  private final boolean available;
  private final List<TagModel> tags;

  public ItemModel(Item item, Map<String, String> categoryName, String symbol) {
    this.id = item.getId().toString();
    this.categoryId = item.getCategory().getId().toString();
    this.categoryName = categoryName;
    this.name = convertMap(item.getName());
    this.description = convertMap(item.getDescription());
    this.price = item.getPrice();
    this.available = item.isAvailable();
    this.images =
        item.getImages().stream().map(img -> new ImageModel(img.getId(), img.getName())).toList();
    this.imageUrls = this.images.stream().map(ImageModel::getUrl).toList();
    this.tags =
        item.getTags() != null
            ? item.getTags().stream()
                .map(
                    t -> {
                      var it =
                          dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag.fromDomain(t);
                      return new TagModel(it.name(), it.getIcon(), it.getLabel());
                    })
                .toList()
            : List.of();
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

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public List<ImageModel> getImages() {
    return images;
  }

  public boolean isAvailable() {
    return available;
  }

  public List<TagModel> getTags() {
    return tags;
  }

  public static class TagModel {
    private final String name;
    private final String icon;
    private final String labelKey;

    public TagModel(String name, String icon, String labelKey) {
      this.name = name;
      this.icon = icon;
      this.labelKey = labelKey;
    }

    public String getName() {
      return name;
    }

    public String getIcon() {
      return icon;
    }

    public String getLabelKey() {
      return labelKey;
    }
  }
}

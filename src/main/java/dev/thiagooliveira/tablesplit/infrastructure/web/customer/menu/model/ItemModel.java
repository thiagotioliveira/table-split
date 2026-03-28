package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.ImageModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemModel {
  private final String id;
  private final String categoryId;
  private final List<ImageModel> images;
  private final Map<String, String> name;
  private final Map<String, String> description;
  private final BigDecimal price;
  private final String priceFormatted;
  private PromotionInfo promotion;

  public ItemModel(Item item, String symbol) {
    this.id = item.getId().toString();
    this.categoryId = item.getCategory().getId().toString();
    this.images =
        item.getImages().stream().map(img -> new ImageModel(img.getId(), img.getName())).toList();
    this.name = convertMap(item.getName());
    this.description = convertMap(item.getDescription());
    this.price = item.getPrice();
    this.priceFormatted = String.format("%s %s", symbol, this.price);
    if (item.getPromotion() != null) {
      this.promotion =
          new PromotionInfo(
              item.getPromotion().promotionId(),
              item.getPromotion().promotionalPrice(),
              item.getPromotion().discountType().name(),
              item.getPromotion().discountValue());
    }
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

  public List<ImageModel> getImages() {
    return images;
  }

  public PromotionInfo getPromotion() {
    return promotion;
  }

  public static class PromotionInfo {
    private final UUID promotionId;
    private final BigDecimal promotionalPrice;
    private final String discountType;
    private final BigDecimal discountValue;

    public PromotionInfo(
        UUID promotionId,
        BigDecimal promotionalPrice,
        String discountType,
        BigDecimal discountValue) {
      this.promotionId = promotionId;
      this.promotionalPrice = promotionalPrice;
      this.discountType = discountType;
      this.discountValue = discountValue;
    }

    public UUID getPromotionId() {
      return promotionId;
    }

    public BigDecimal getPromotionalPrice() {
      return promotionalPrice;
    }

    public String getDiscountType() {
      return discountType;
    }

    public BigDecimal getDiscountValue() {
      return discountValue;
    }
  }
}

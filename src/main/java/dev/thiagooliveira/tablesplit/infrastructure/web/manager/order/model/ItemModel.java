package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ItemModel {
  private final String id;
  private final Map<String, String> name;
  private final BigDecimal price;
  private final String categoryId;
  private final PromotionModel promotion;

  public ItemModel(
      UUID id,
      Map<String, String> name,
      BigDecimal price,
      UUID categoryId,
      PromotionModel promotion) {
    this.id = id.toString();
    this.name = name;
    this.price = price;
    this.categoryId = categoryId.toString();
    this.promotion = promotion;
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

  public PromotionModel getPromotion() {
    return promotion;
  }

  public record PromotionModel(
      UUID promotionId,
      BigDecimal promotionalPrice,
      String discountType,
      BigDecimal discountValue) {}
}

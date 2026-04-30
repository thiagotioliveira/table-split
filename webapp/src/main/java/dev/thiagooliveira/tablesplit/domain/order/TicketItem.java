package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TicketItem {

  private UUID id;
  private UUID itemId;
  private Map<Language, String> name = new java.util.HashMap<>();
  private UUID customerId;
  private int quantity;
  private BigDecimal unitPrice;
  private String note;
  private Integer rating;
  private TicketStatus status = TicketStatus.PENDING;
  private PromotionSnapshot promotionSnapshot;
  private List<TicketItemCustomization> customizations;

  public TicketItem() {}

  public TicketItem(Item item, int quantity, UUID customerId, String note) {
    this(item, quantity, customerId, note, null, null, null, null);
  }

  public TicketItem(
      Item item,
      int quantity,
      UUID customerId,
      String note,
      List<TicketItemCustomization> customizations,
      UUID promotionId,
      String discountType,
      BigDecimal discountValue) {
    this.id = UUID.randomUUID();
    this.itemId = item.getId();
    this.name = item.getName();
    this.customerId = customerId;
    this.quantity = quantity;
    this.note = note;
    this.customizations = customizations;

    BigDecimal extra = calculateExtraPrice(customizations);
    BigDecimal basePriceWithExtra = item.getPrice().add(extra);
    BigDecimal finalUnitPrice;

    if (promotionId == null && item.getPromotion() != null) {
      promotionId = item.getPromotion().promotionId();
      discountType = item.getPromotion().discountType().name();
      discountValue = item.getPromotion().discountValue();
    }

    if (promotionId != null) {
      BigDecimal promotionalPrice = BigDecimal.ZERO;
      if (DiscountType.PERCENTAGE.name().equals(discountType)) {
        BigDecimal discount =
            item.getPrice()
                .multiply(discountValue)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        promotionalPrice = item.getPrice().subtract(discount).add(extra);
      } else if (DiscountType.FIXED_VALUE.name().equals(discountType)) {
        promotionalPrice = item.getPrice().subtract(discountValue).max(BigDecimal.ZERO).add(extra);
      }

      finalUnitPrice = promotionalPrice;
      this.promotionSnapshot =
          new PromotionSnapshot(promotionId, basePriceWithExtra, discountType, discountValue);
    } else {
      finalUnitPrice = item.getPrice().add(extra);
    }
    this.unitPrice = finalUnitPrice;
  }

  private java.math.BigDecimal calculateExtraPrice(List<TicketItemCustomization> customizations) {
    if (customizations == null) return java.math.BigDecimal.ZERO;
    java.math.BigDecimal extra = java.math.BigDecimal.ZERO;
    for (TicketItemCustomization customization : customizations) {
      extra = extra.add(customization.calculateTotalExtra());
    }
    return extra;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getTotalPrice() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }

  public PromotionSnapshot getPromotionSnapshot() {
    return promotionSnapshot;
  }

  public void setPromotionSnapshot(PromotionSnapshot promotionSnapshot) {
    this.promotionSnapshot = promotionSnapshot;
  }

  public List<TicketItemCustomization> getCustomizations() {
    return customizations;
  }

  public void setCustomizations(List<TicketItemCustomization> customizations) {
    this.customizations = customizations;
  }

  public boolean isPending() {
    return status != null && status.isPending();
  }

  public boolean isPreparing() {
    return status != null && status.isPreparing();
  }

  public boolean isReady() {
    return status != null && status.isReady();
  }

  public boolean isDelivered() {
    return status != null && status.isDelivered();
  }

  public boolean isCancelled() {
    return status != null && status.isCancelled();
  }

  public record PromotionSnapshot(
      UUID promotionId, BigDecimal originalPrice, String discountType, BigDecimal discountValue) {

    public BigDecimal calculatePromotionalPrice(BigDecimal unitPrice) {
      return unitPrice;
    }
  }
}

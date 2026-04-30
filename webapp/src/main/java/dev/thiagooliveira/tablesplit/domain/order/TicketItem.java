package dev.thiagooliveira.tablesplit.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class TicketItem {
  private ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

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
  private String customizations;

  public TicketItem() {}

  public TicketItem(Item item, int quantity, UUID customerId, String note) {
    this(item, quantity, customerId, note, null, null, null, null);
  }

  public TicketItem(
      Item item,
      int quantity,
      UUID customerId,
      String note,
      String customizations,
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

    BigDecimal finalUnitPrice = item.getPrice();

    if (promotionId == null && item.getPromotion() != null) {
      promotionId = item.getPromotion().promotionId();
      discountType = item.getPromotion().discountType().name();
      discountValue = item.getPromotion().discountValue();
    }

    if (promotionId != null) {
      this.promotionSnapshot =
          new PromotionSnapshot(promotionId, item.getPrice(), discountType, discountValue);

      if (DiscountType.PERCENTAGE.name().equals(discountType)) {
        BigDecimal discount =
            item.getPrice()
                .multiply(discountValue)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        finalUnitPrice = item.getPrice().subtract(discount);
      } else if (DiscountType.FIXED_VALUE.name().equals(discountType)) {
        finalUnitPrice = item.getPrice().subtract(discountValue).max(BigDecimal.ZERO);
      }
    }

    // Handle extra prices from customizations
    if (this.customizations != null && !this.customizations.isEmpty()) {
      BigDecimal extra = calculateExtraPrice(customizations);
      finalUnitPrice = finalUnitPrice.add(extra);
    }
    this.unitPrice = finalUnitPrice;
  }

  private java.math.BigDecimal calculateExtraPrice(String customizationsJson) {
    java.math.BigDecimal extra = java.math.BigDecimal.ZERO;
    try {
      var root = objectMapper.readTree(customizationsJson);
      if (root.isArray()) {
        for (var question : root) {
          var options = question.get("options");
          if (options != null && options.isArray()) {
            for (var option : options) {
              var extraPrice = option.get("extraPrice");
              if (extraPrice != null && !extraPrice.isNull()) {
                extra = extra.add(new java.math.BigDecimal(extraPrice.asText()));
              }
            }
          }
        }
      }
    } catch (Exception e) {
      // Ignore errors in customizations parsing
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
    BigDecimal customizationsExtra = BigDecimal.ZERO;
    if (customizations != null && !customizations.isEmpty()) {
      try {
        // Simple manual parsing to avoid heavy dependencies if possible,
        // but since we have Jackson in the project, we might use it.
        // For now, let's assume the unitPrice is already updated by the caller or we'll update it.
      } catch (Exception e) {
      }
    }
    return unitPrice.add(customizationsExtra).multiply(BigDecimal.valueOf(quantity));
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

  public String getCustomizations() {
    return customizations;
  }

  public void setCustomizations(String customizations) {
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
      UUID promotionId, BigDecimal originalPrice, String discountType, BigDecimal discountValue) {}
}

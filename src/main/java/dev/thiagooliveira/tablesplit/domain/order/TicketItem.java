package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
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
  private TicketStatus status = TicketStatus.PENDING;
  private PromotionSnapshot promotionSnapshot;

  public TicketItem() {}

  public TicketItem(Item item, int quantity, UUID customerId, String note) {
    this.id = UUID.randomUUID();
    this.itemId = item.getId();
    this.name = item.getName();
    this.customerId = customerId;
    this.quantity = quantity;
    this.unitPrice = item.getEffectivePrice();
    this.note = note;

    // Capture promotion snapshot if item has promotion
    if (item.getPromotion() != null) {
      this.promotionSnapshot =
          new PromotionSnapshot(
              item.getPromotion().promotionId(),
              item.getPrice(), // original price
              item.getPromotion().discountType().name(),
              item.getPromotion().discountValue());
    }
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

  public record PromotionSnapshot(
      UUID promotionId, BigDecimal originalPrice, String discountType, BigDecimal discountValue) {}
}

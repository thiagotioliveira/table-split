package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.UUID;

public class TicketItemRequest {
  private UUID itemId;
  private UUID customerId;
  private int quantity;
  private String note;
  private UUID promotionId;
  private java.math.BigDecimal originalPrice;
  private String discountType;
  private java.math.BigDecimal discountValue;
  private String customizations;

  public TicketItemRequest() {}

  public TicketItemRequest(UUID itemId, int quantity, String note) {
    this(itemId, null, quantity, note);
  }

  public TicketItemRequest(UUID itemId, UUID customerId, int quantity, String note) {
    this.itemId = itemId;
    this.customerId = customerId;
    this.quantity = quantity;
    this.note = note;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
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

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public UUID getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(UUID promotionId) {
    this.promotionId = promotionId;
  }

  public java.math.BigDecimal getOriginalPrice() {
    return originalPrice;
  }

  public void setOriginalPrice(java.math.BigDecimal originalPrice) {
    this.originalPrice = originalPrice;
  }

  public String getDiscountType() {
    return discountType;
  }

  public void setDiscountType(String discountType) {
    this.discountType = discountType;
  }

  public java.math.BigDecimal getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(java.math.BigDecimal discountValue) {
    this.discountValue = discountValue;
  }

  public String getCustomizations() {
    return customizations;
  }

  public void setCustomizations(String customizations) {
    this.customizations = customizations;
  }
}

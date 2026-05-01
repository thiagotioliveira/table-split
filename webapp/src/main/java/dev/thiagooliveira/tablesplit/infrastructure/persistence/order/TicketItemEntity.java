package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "ticket_items")
public class TicketItemEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = false)
  private TicketEntity ticket;

  @Column(nullable = false)
  private UUID itemId;

  @Column(nullable = false)
  private UUID customerId;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal unitPrice;

  @Column private String note;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TicketStatus status;

  // Promotion fields - snapshot of promotion at time of order
  @Column private UUID promotionId;

  @Column(precision = 19, scale = 2)
  private BigDecimal originalPrice;

  @Column private String discountType;

  @Column(precision = 19, scale = 2)
  private BigDecimal discountValue;

  @Column private Integer rating;

  @Column(columnDefinition = "TEXT")
  @Convert(converter = TicketItemCustomizationConverter.class)
  private java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
      customizations;

  public TicketItemEntity() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public TicketEntity getTicket() {
    return ticket;
  }

  public void setTicket(TicketEntity ticket) {
    this.ticket = ticket;
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

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
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

  public UUID getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(UUID promotionId) {
    this.promotionId = promotionId;
  }

  public BigDecimal getOriginalPrice() {
    return originalPrice;
  }

  public void setOriginalPrice(BigDecimal originalPrice) {
    this.originalPrice = originalPrice;
  }

  public String getDiscountType() {
    return discountType;
  }

  public void setDiscountType(String discountType) {
    this.discountType = discountType;
  }

  public BigDecimal getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
      getCustomizations() {
    return customizations;
  }

  public void setCustomizations(
      java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
          customizations) {
    this.customizations = customizations;
  }
}

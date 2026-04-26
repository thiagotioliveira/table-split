package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
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
  @Column private String customizations;

  public TicketItemEntity() {}

  public TicketItem toDomain() {
    TicketItem domain = new TicketItem();
    domain.setId(this.id);
    domain.setItemId(this.itemId);
    domain.setName(new HashMap<>()); // Nome será obtido do Item quando necessário
    domain.setCustomerId(this.customerId);
    domain.setQuantity(this.quantity);
    domain.setUnitPrice(this.unitPrice);
    domain.setNote(this.note);
    domain.setRating(this.rating);
    domain.setStatus(this.status);
    domain.setCustomizations(this.customizations);

    // Restore promotion snapshot if exists
    if (this.discountType != null) {
      domain.setPromotionSnapshot(
          new TicketItem.PromotionSnapshot(
              this.promotionId, this.originalPrice, this.discountType, this.discountValue));
    }

    return domain;
  }

  public static TicketItemEntity fromDomain(TicketItem domain, TicketEntity ticket) {
    TicketItemEntity entity = new TicketItemEntity();
    entity.setId(domain.getId());
    entity.setTicket(ticket);
    entity.setItemId(domain.getItemId());
    entity.setCustomerId(domain.getCustomerId());
    entity.setQuantity(domain.getQuantity());
    entity.setUnitPrice(domain.getUnitPrice());
    entity.setNote(domain.getNote());
    entity.setRating(domain.getRating());
    entity.setStatus(domain.getStatus());
    entity.setCustomizations(domain.getCustomizations());

    // Save promotion snapshot if exists
    if (domain.getPromotionSnapshot() != null) {
      var snapshot = domain.getPromotionSnapshot();
      entity.setPromotionId(snapshot.promotionId());
      entity.setOriginalPrice(snapshot.originalPrice());
      entity.setDiscountType(snapshot.discountType());
      entity.setDiscountValue(snapshot.discountValue());
    }

    return entity;
  }

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

  public String getCustomizations() {
    return customizations;
  }

  public void setCustomizations(String customizations) {
    this.customizations = customizations;
  }
}

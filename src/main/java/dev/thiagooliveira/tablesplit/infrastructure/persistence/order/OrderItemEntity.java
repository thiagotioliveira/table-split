package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderItem;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "order_items")
public class OrderItemEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @Column(nullable = false)
  private UUID itemId;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "name_localized_text_id")
  private LocalizedTextEntity name;

  @Column(nullable = false)
  private String customerName;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  @Column private String note;

  public OrderItemEntity() {}

  public OrderItem toDomain() {
    OrderItem domain = new OrderItem();
    domain.setId(this.id);
    domain.setItemId(this.itemId);
    domain.setName(this.name != null ? this.name.getTranslations() : new HashMap<>());
    domain.setCustomerName(this.customerName);
    domain.setQuantity(this.quantity);
    domain.setUnitPrice(this.unitPrice);
    domain.setNote(this.note);
    return domain;
  }

  public static OrderItemEntity fromDomain(OrderItem domain, OrderEntity order) {
    OrderItemEntity entity = new OrderItemEntity();
    entity.setId(domain.getId());
    entity.setOrder(order);
    entity.setItemId(domain.getItemId());
    entity.setName(LocalizedTextEntity.fromMap(domain.getName()));
    entity.setCustomerName(domain.getCustomerName());
    entity.setQuantity(domain.getQuantity());
    entity.setUnitPrice(domain.getUnitPrice());
    entity.setNote(domain.getNote());
    return entity;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OrderEntity getOrder() {
    return order;
  }

  public void setOrder(OrderEntity order) {
    this.order = order;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
  }

  public LocalizedTextEntity getName() {
    return name;
  }

  public void setName(LocalizedTextEntity name) {
    this.name = name;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
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
}

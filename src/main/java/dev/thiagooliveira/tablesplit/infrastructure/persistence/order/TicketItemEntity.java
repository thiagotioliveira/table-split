package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity;
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

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TicketStatus status;

  public TicketItemEntity() {}

  public TicketItem toDomain() {
    TicketItem domain = new TicketItem();
    domain.setId(this.id);
    domain.setItemId(this.itemId);
    domain.setName(this.name != null ? this.name.getTranslations() : new HashMap<>());
    domain.setCustomerName(this.customerName);
    domain.setQuantity(this.quantity);
    domain.setUnitPrice(this.unitPrice);
    domain.setNote(this.note);
    domain.setStatus(this.status);
    return domain;
  }

  public static TicketItemEntity fromDomain(TicketItem domain, TicketEntity ticket) {
    TicketItemEntity entity = new TicketItemEntity();
    entity.setId(domain.getId());
    entity.setTicket(ticket);
    entity.setItemId(domain.getItemId());
    entity.setName(LocalizedTextEntity.fromMap(domain.getName()));
    entity.setCustomerName(domain.getCustomerName());
    entity.setQuantity(domain.getQuantity());
    entity.setUnitPrice(domain.getUnitPrice());
    entity.setNote(domain.getNote());
    entity.setStatus(domain.getStatus());
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

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }
}

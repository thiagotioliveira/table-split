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
    domain.setName(new HashMap<>()); // Nome será obtido do Item quando necessário
    domain.setCustomerId(this.customerId);
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
    entity.setCustomerId(domain.getCustomerId());
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
}

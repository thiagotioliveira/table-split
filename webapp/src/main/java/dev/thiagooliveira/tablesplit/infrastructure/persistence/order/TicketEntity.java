package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "tickets")
public class TicketEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TicketItemEntity> items = new ArrayList<>();

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TicketStatus status;

  @Column(nullable = false)
  private ZonedDateTime createdAt;

  private ZonedDateTime readyAt;

  @Column private String note;

  public TicketEntity() {}

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

  public List<TicketItemEntity> getItems() {
    return items;
  }

  public void setItems(List<TicketItemEntity> items) {
    this.items = items;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ZonedDateTime getReadyAt() {
    return readyAt;
  }

  public void setReadyAt(ZonedDateTime readyAt) {
    this.readyAt = readyAt;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}

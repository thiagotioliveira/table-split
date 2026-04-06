package dev.thiagooliveira.tablesplit.domain.order;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ticket {
  private UUID id;
  private List<TicketItem> items = new ArrayList<>();
  private TicketStatus status;
  private ZonedDateTime createdAt;
  private ZonedDateTime readyAt;
  private String note;

  public Ticket() {
    this.id = UUID.randomUUID();
    this.status = TicketStatus.PENDING;
    this.createdAt = ZonedDateTime.now();
  }

  public Ticket(
      UUID id, List<TicketItem> items, TicketStatus status, ZonedDateTime createdAt, String note) {
    this.id = id;
    this.items = items;
    this.status = status;
    this.createdAt = createdAt;
    this.note = note;
  }

  public BigDecimal calculateTotal() {
    return items.stream()
        .filter(item -> item.getStatus() != TicketStatus.CANCELLED)
        .map(TicketItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public List<TicketItem> getItems() {
    return items;
  }

  public void setItems(List<TicketItem> items) {
    this.items = items;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
    if (this.items != null) {
      this.items.forEach(item -> item.setStatus(status));
    }
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

  public void recalculateStatus() {
    if (items == null || items.isEmpty()) {
      return;
    }

    boolean anyInProduction =
        items.stream()
            .anyMatch(
                item ->
                    item.getStatus() == TicketStatus.PREPARING
                        || item.getStatus() == TicketStatus.READY);

    if (anyInProduction && this.status == TicketStatus.PENDING) {
      this.status = TicketStatus.PREPARING;
    }

    boolean allCompleted =
        items.stream()
            .allMatch(
                item ->
                    item.getStatus() == TicketStatus.DELIVERED
                        || item.getStatus() == TicketStatus.CANCELLED);

    if (allCompleted) {
      boolean atLeastOneDelivered =
          items.stream().anyMatch(item -> item.getStatus() == TicketStatus.DELIVERED);

      if (atLeastOneDelivered) {
        this.status = TicketStatus.DELIVERED;
      } else {
        this.status = TicketStatus.CANCELLED;
      }
    }
  }
}

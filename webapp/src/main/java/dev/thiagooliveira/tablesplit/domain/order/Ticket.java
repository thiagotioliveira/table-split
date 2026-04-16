package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.Time;
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
    this.createdAt = Time.now();
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
        .filter(item -> !item.isCancelled())
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
      this.items.stream()
          .filter(item -> !item.isCancelled() && !item.isDelivered())
          .forEach(item -> item.setStatus(status));
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
            .anyMatch(item -> item.getStatus().isPreparing() || item.getStatus().isReady());

    if (anyInProduction && isPending()) {
      this.status = TicketStatus.PREPARING;
    }

    boolean allCompleted =
        items.stream()
            .allMatch(item -> item.getStatus().isDelivered() || item.getStatus().isCancelled());

    if (allCompleted) {
      boolean atLeastOneDelivered = items.stream().anyMatch(item -> item.getStatus().isDelivered());

      if (atLeastOneDelivered) {
        this.status = TicketStatus.DELIVERED;
      } else {
        this.status = TicketStatus.CANCELLED;
      }
    }
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
}

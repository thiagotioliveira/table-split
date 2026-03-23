package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class TicketModel {
  private final UUID id;
  private final String shortId;
  private final String tableCod;
  private final String customerName;
  private final TicketStatus status;
  private final String statusClass;
  private final String statusLabel;
  private final ZonedDateTime createdAt;
  private final String timeAgo;
  private final List<TicketItemModel> items;
  private final BigDecimal total;
  private final boolean urgent;

  public TicketModel(
      UUID id,
      String tableCod,
      String customerName,
      TicketStatus status,
      ZonedDateTime createdAt,
      String timeAgo,
      List<TicketItemModel> items,
      BigDecimal total,
      boolean urgent) {
    this.id = id;
    this.shortId = id.toString().substring(0, 4).toUpperCase();
    this.tableCod = tableCod;
    this.customerName = customerName;
    this.status = status;
    this.statusClass = status.getCssClass();
    this.statusLabel = status.getLabel();
    this.createdAt = createdAt;
    this.timeAgo = timeAgo;
    this.items = items;
    this.total = total;
    this.urgent = urgent;
  }

  public UUID getId() {
    return id;
  }

  public String getShortId() {
    return shortId;
  }

  public String getTableCod() {
    return tableCod;
  }

  public String getCustomerName() {
    return customerName;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public String getStatusClass() {
    return statusClass;
  }

  public String getStatusLabel() {
    return statusLabel;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public String getTimeAgo() {
    return timeAgo;
  }

  public List<TicketItemModel> getItems() {
    return items;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public boolean isUrgent() {
    return urgent;
  }
}

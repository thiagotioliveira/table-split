package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.util.List;

public class OrderHistoryModel {
  private final String id;
  private final String tableId;
  private final Integer serviceFee;
  private final String status;
  private final String openedAt;
  private final String closedAt;
  private final List<TicketItemModel> items;
  private final List<OrderHistoryPaymentModel> payments;

  public OrderHistoryModel(
      String id,
      String tableId,
      Integer serviceFee,
      String status,
      String openedAt,
      String closedAt,
      List<TicketItemModel> items,
      List<OrderHistoryPaymentModel> payments) {
    this.id = id;
    this.tableId = tableId;
    this.serviceFee = serviceFee;
    this.status = status;
    this.openedAt = openedAt;
    this.closedAt = closedAt;
    this.items = items;
    this.payments = payments;
  }

  public String getId() {
    return id;
  }

  public String getTableId() {
    return tableId;
  }

  public Integer getServiceFee() {
    return serviceFee;
  }

  public String getStatus() {
    return status;
  }

  public String getOpenedAt() {
    return openedAt;
  }

  public String getClosedAt() {
    return closedAt;
  }

  public List<TicketItemModel> getItems() {
    return items;
  }

  public List<OrderHistoryPaymentModel> getPayments() {
    return payments;
  }
}

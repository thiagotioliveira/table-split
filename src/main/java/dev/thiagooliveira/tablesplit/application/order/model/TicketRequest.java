package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.List;
import java.util.UUID;

public class TicketRequest {
  private String customerName;
  private UUID customerId;
  private String note;
  private List<TicketItemRequest> items;

  public TicketRequest() {}

  public TicketRequest(
      UUID customerId, String customerName, String note, List<TicketItemRequest> items) {
    this.customerId = customerId;
    this.customerName = customerName;
    this.note = note;
    this.items = items;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public List<TicketItemRequest> getItems() {
    return items;
  }

  public void setItems(List<TicketItemRequest> items) {
    this.items = items;
  }
}

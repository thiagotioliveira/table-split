package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.List;

public class TicketRequest {
  private String customerName;
  private String note;
  private List<OrderItemRequest> items;

  public TicketRequest() {}

  public TicketRequest(String customerName, String note, List<OrderItemRequest> items) {
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

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public List<OrderItemRequest> getItems() {
    return items;
  }

  public void setItems(List<OrderItemRequest> items) {
    this.items = items;
  }
}

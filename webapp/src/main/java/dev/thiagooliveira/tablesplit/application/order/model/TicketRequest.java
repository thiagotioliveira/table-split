package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.List;

public class TicketRequest {
  private String note;
  private List<TicketItemRequest> items;

  public TicketRequest() {}

  public TicketRequest(String note, List<TicketItemRequest> items) {
    this.note = note;
    this.items = items;
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

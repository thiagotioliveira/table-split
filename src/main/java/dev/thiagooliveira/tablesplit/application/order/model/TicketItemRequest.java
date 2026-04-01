package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.UUID;

public class TicketItemRequest {
  private UUID itemId;
  private UUID customerId;
  private int quantity;
  private String note;

  public TicketItemRequest() {}

  public TicketItemRequest(UUID itemId, int quantity, String note) {
    this(itemId, null, quantity, note);
  }

  public TicketItemRequest(UUID itemId, UUID customerId, int quantity, String note) {
    this.itemId = itemId;
    this.customerId = customerId;
    this.quantity = quantity;
    this.note = note;
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

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}

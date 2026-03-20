package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.UUID;

public class OrderItemRequest {
  private UUID itemId;
  private int quantity;
  private String note;

  public OrderItemRequest() {}

  public OrderItemRequest(UUID itemId, int quantity, String note) {
    this.itemId = itemId;
    this.quantity = quantity;
    this.note = note;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
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

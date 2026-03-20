package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.UUID;

public class OrderItemRequest {
  private UUID itemId;
  private int quantity;

  public OrderItemRequest() {}

  public OrderItemRequest(UUID itemId, int quantity) {
    this.itemId = itemId;
    this.quantity = quantity;
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
}

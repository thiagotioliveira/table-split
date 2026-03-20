package dev.thiagooliveira.tablesplit.infrastructure.web.customer.order.model;

import java.util.List;
import java.util.UUID;

public class CustomerOrderRequest {
  private String customerName;
  private List<CustomerOrderItemRequest> items;

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public List<CustomerOrderItemRequest> getItems() {
    return items;
  }

  public void setItems(List<CustomerOrderItemRequest> items) {
    this.items = items;
  }

  public static class CustomerOrderItemRequest {
    private UUID itemId;
    private int quantity;

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
}

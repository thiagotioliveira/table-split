package dev.thiagooliveira.tablesplit.infrastructure.web.customer.order.model;

import java.util.List;
import java.util.UUID;

public class CustomerOrderRequest {
  private String customerName;
  private List<CustomerTicketItemRequest> items;

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public List<CustomerTicketItemRequest> getItems() {
    return items;
  }

  public void setItems(List<CustomerTicketItemRequest> items) {
    this.items = items;
  }

  public static class CustomerTicketItemRequest {
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

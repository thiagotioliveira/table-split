package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.List;
import java.util.UUID;

public class PlaceOrderRequest {
  private UUID restaurantId;
  private int serviceFee;
  private String tableCod;
  private String customerName;
  private List<OrderItemRequest> items;

  public PlaceOrderRequest() {}

  public PlaceOrderRequest(
      UUID restaurantId,
      String tableCod,
      String customerName,
      List<OrderItemRequest> items,
      int serviceFee) {
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.customerName = customerName;
    this.items = items;
    this.serviceFee = serviceFee;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getTableCod() {
    return tableCod;
  }

  public void setTableCod(String tableCod) {
    this.tableCod = tableCod;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public List<OrderItemRequest> getItems() {
    return items;
  }

  public void setItems(List<OrderItemRequest> items) {
    this.items = items;
  }

  public int getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(int serviceFee) {
    this.serviceFee = serviceFee;
  }
}

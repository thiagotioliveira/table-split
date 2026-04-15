package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.List;
import java.util.UUID;

public class PlaceOrderRequest {
  private UUID restaurantId;
  private Integer serviceFee;
  private String tableCod;
  private List<TicketRequest> tickets;
  private List<CustomerRequest> customers;

  public PlaceOrderRequest() {}

  public PlaceOrderRequest(
      UUID restaurantId,
      String tableCod,
      List<TicketRequest> tickets,
      Integer serviceFee,
      List<CustomerRequest> customers) {
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.tickets = tickets;
    this.serviceFee = serviceFee;
    this.customers = customers;
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

  public List<TicketRequest> getTickets() {
    return tickets;
  }

  public void setTickets(List<TicketRequest> tickets) {
    this.tickets = tickets;
  }

  public Integer getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(Integer serviceFee) {
    this.serviceFee = serviceFee;
  }

  public List<CustomerRequest> getCustomers() {
    return customers;
  }

  public void setCustomers(List<CustomerRequest> customers) {
    this.customers = customers;
  }
}

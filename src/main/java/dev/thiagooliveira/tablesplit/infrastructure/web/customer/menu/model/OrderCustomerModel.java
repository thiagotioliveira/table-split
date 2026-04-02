package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;

public class OrderCustomerModel {
  private final String id;
  private final String name;

  public OrderCustomerModel(OrderCustomer customer) {
    this.id = customer.getId().toString();
    this.name = customer.getName();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}

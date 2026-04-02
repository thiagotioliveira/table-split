package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;
import java.util.UUID;

public class OrderCustomerModel {
  private final UUID id;
  private final String name;

  public OrderCustomerModel(OrderCustomer customer) {
    this.id = customer.getId();
    this.name = customer.getName();
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}

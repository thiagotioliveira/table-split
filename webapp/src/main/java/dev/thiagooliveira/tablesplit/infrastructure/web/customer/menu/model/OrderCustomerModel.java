package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;
import java.math.BigDecimal;

public class OrderCustomerModel {
  private final String id;
  private final String name;
  private final BigDecimal subtotal;

  public OrderCustomerModel(OrderCustomer customer, BigDecimal subtotal) {
    this.id = customer.getId().toString();
    this.name = customer.getName();
    this.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public java.math.BigDecimal getSubtotal() {
    return subtotal;
  }
}

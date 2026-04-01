package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class OrderCustomerEntity {

  @Column(name = "customer_id", nullable = false)
  private UUID id;

  @Column(name = "customer_name", nullable = false)
  private String name;

  public OrderCustomerEntity() {}

  public OrderCustomerEntity(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public OrderCustomer toDomain() {
    return new OrderCustomer(this.id, this.name);
  }

  public static OrderCustomerEntity fromDomain(OrderCustomer domain) {
    return new OrderCustomerEntity(domain.getId(), domain.getName());
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

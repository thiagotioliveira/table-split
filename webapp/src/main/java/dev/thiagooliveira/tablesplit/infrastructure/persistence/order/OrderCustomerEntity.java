package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderCustomerEntity that = (OrderCustomerEntity) o;
    return java.util.Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(id);
  }
}

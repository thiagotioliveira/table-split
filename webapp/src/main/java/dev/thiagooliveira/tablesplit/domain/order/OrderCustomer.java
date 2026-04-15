package dev.thiagooliveira.tablesplit.domain.order;

import java.util.Objects;
import java.util.UUID;

public class OrderCustomer {
  private UUID id;
  private String name;

  public OrderCustomer() {}

  public OrderCustomer(UUID id, String name) {
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
    OrderCustomer that = (OrderCustomer) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.util.Objects;
import java.util.UUID;

public class CustomerModel {
  private final UUID id;
  private final String name;

  public CustomerModel(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomerModel that = (CustomerModel) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

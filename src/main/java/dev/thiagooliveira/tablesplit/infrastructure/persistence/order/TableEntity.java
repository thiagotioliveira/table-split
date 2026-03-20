package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "tables")
public class TableEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID restaurantId;

  @Column(nullable = false)
  private String cod;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TableStatus status;

  public TableEntity() {}

  public Table toDomain() {
    Table domain = new Table();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setCod(this.cod);
    domain.setStatus(this.status);
    return domain;
  }

  public static TableEntity fromDomain(Table domain) {
    TableEntity entity = new TableEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setCod(domain.getCod());
    entity.setStatus(domain.getStatus());
    return entity;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getCod() {
    return cod;
  }

  public void setCod(String cod) {
    this.cod = cod;
  }

  public TableStatus getStatus() {
    return status;
  }

  public void setStatus(TableStatus status) {
    this.status = status;
  }
}

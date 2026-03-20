package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "orders")
public class OrderEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID restaurantId;

  @Column(nullable = false)
  private UUID tableId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItemEntity> items = new ArrayList<>();

  @Column(nullable = false)
  private ZonedDateTime openedAt;

  private ZonedDateTime closedAt;

  public OrderEntity() {}

  public Order toDomain() {
    Order domain = new Order();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setTableId(this.tableId);
    domain.setStatus(this.status);
    domain.setOpenedAt(this.openedAt);
    domain.setClosedAt(this.closedAt);
    if (this.items != null) {
      domain.setItems(this.items.stream().map(OrderItemEntity::toDomain).toList());
    }
    return domain;
  }

  public static OrderEntity fromDomain(Order domain) {
    OrderEntity entity = new OrderEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setTableId(domain.getTableId());
    entity.setStatus(domain.getStatus());
    entity.setOpenedAt(domain.getOpenedAt());
    entity.setClosedAt(domain.getClosedAt());
    if (domain.getItems() != null) {
      entity.setItems(
          new ArrayList<>(
              domain.getItems().stream()
                  .map(item -> OrderItemEntity.fromDomain(item, entity))
                  .toList()));
    }
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

  public UUID getTableId() {
    return tableId;
  }

  public void setTableId(UUID tableId) {
    this.tableId = tableId;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public List<OrderItemEntity> getItems() {
    return items;
  }

  public void setItems(List<OrderItemEntity> items) {
    this.items = items;
  }

  public ZonedDateTime getOpenedAt() {
    return openedAt;
  }

  public void setOpenedAt(ZonedDateTime openedAt) {
    this.openedAt = openedAt;
  }

  public ZonedDateTime getClosedAt() {
    return closedAt;
  }

  public void setClosedAt(ZonedDateTime closedAt) {
    this.closedAt = closedAt;
  }
}

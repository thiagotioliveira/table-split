package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
  private UUID id;
  private UUID restaurantId;
  private UUID tableId;
  private OrderStatus status;
  private List<OrderItem> items = new ArrayList<>();
  private ZonedDateTime openedAt;
  private ZonedDateTime closedAt;

  public Order() {}

  public Order(UUID id, UUID restaurantId, UUID tableId) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableId = tableId;
    this.status = OrderStatus.OPEN;
    this.openedAt = ZonedDateTime.now();
  }

  public void addItem(Item item, int quantity, String customerName, String note) {
    if (this.status != OrderStatus.OPEN) {
      throw new IllegalStateException("Cannot add items to a non-open order");
    }
    this.items.add(new OrderItem(item, quantity, customerName, note));
  }

  public BigDecimal calculateTotal() {
    return items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void close() {
    if (this.status != OrderStatus.OPEN && this.status != OrderStatus.PENDING) {
      throw new IllegalStateException(
          "Cannot close an order that is already finished or cancelled");
    }
    this.status = OrderStatus.CLOSED;
    this.closedAt = ZonedDateTime.now();
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

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
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

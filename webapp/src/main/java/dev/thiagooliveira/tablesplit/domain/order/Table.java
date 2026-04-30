package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Table extends AggregateRoot {
  private UUID id;
  private UUID restaurantId;
  private String cod;
  private TableStatus status;
  private OffsetDateTime deletedAt;

  // Transient accountId used for event publishing
  private transient UUID accountId;

  public Table() {}

  public Table(UUID id, UUID restaurantId, String cod) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.cod = cod;
    this.status = TableStatus.AVAILABLE;
  }

  public static Table create(UUID restaurantId, String cod) {
    return create(UUID.randomUUID(), restaurantId, cod);
  }

  public static Table create(UUID id, UUID restaurantId, String cod) {
    Table table = new Table(id, restaurantId, cod);
    table.registerEvent(new dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent(table));
    return table;
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

  public OffsetDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(OffsetDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  public void softDelete() {
    this.deletedAt = Time.nowOffset();
    this.status = TableStatus.AVAILABLE;
  }

  public void restore() {
    this.deletedAt = null;
    this.status = TableStatus.AVAILABLE;
    this.registerEvent(new dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent(this));
  }

  public void callWaiter() {
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.WaiterCalledEvent(
            this.accountId, this.restaurantId, this.cod));
  }

  public void occupy() {
    this.status = TableStatus.OCCUPIED;
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent(this));
  }

  public void waiting() {
    this.status = TableStatus.WAITING;
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent(this));
  }

  public void release() {
    this.status = TableStatus.AVAILABLE;
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent(this));
  }

  public void syncStatus(OrderStatus orderStatus) {
    if (orderStatus == OrderStatus.CLOSED || orderStatus == OrderStatus.CANCELLED) {
      release();
    } else if (orderStatus == OrderStatus.WAITING) {
      waiting();
    } else {
      occupy();
    }
  }

  public boolean isAvailable() {
    return this.status == TableStatus.AVAILABLE;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }
}

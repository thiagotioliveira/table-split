package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Table {
  private UUID id;
  private UUID restaurantId;
  private String cod;
  private TableStatus status;
  private OffsetDateTime deletedAt;

  public Table() {}

  public Table(UUID id, UUID restaurantId, String cod) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.cod = cod;
    this.status = TableStatus.AVAILABLE;
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
  }

  public void occupy() {
    this.status = TableStatus.OCCUPIED;
  }

  public void waiting() {
    this.status = TableStatus.WAITING;
  }

  public void release() {
    this.status = TableStatus.AVAILABLE;
  }

  public boolean isAvailable() {
    return this.status == TableStatus.AVAILABLE;
  }
}

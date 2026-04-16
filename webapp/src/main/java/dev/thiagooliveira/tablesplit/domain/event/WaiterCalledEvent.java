package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class WaiterCalledEvent implements DomainEvent {
  private final UUID restaurantId;
  private final String tableCod;
  private final long totalCount;

  private final UUID id;

  public WaiterCalledEvent(UUID restaurantId, String tableCod) {
    this(restaurantId, tableCod, 0, null);
  }

  public WaiterCalledEvent(UUID restaurantId, String tableCod, long totalCount, UUID id) {
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
    this.totalCount = totalCount;
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  @Override
  public UUID getAccountId() {
    return restaurantId;
  }

  public String getTableCod() {
    return tableCod;
  }

  public long getTotalCount() {
    return totalCount;
  }

  public UUID getId() {
    return id;
  }
}

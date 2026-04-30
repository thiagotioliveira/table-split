package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class WaiterCalledEvent implements DomainEvent {
  private final UUID restaurantId;
  private final String tableCod;
  private final long totalCount;

  private final UUID id;

  private final UUID accountId;

  public WaiterCalledEvent(UUID accountId, UUID restaurantId, String tableCod) {
    this(accountId, restaurantId, tableCod, 0, null);
  }

  public WaiterCalledEvent(
      UUID accountId, UUID restaurantId, String tableCod, long totalCount, UUID id) {
    this.accountId = accountId;
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
    return accountId;
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

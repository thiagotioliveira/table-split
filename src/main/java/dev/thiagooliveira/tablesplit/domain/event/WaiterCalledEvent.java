package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class WaiterCalledEvent implements DomainEvent {
  private final UUID restaurantId;
  private final String tableCod;

  public WaiterCalledEvent(UUID restaurantId, String tableCod) {
    this.restaurantId = restaurantId;
    this.tableCod = tableCod;
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
}

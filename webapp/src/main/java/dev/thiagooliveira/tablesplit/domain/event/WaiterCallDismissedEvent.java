package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public class WaiterCallDismissedEvent implements DomainEvent {
  private final UUID restaurantId;
  private final UUID callId;
  private final long totalCount;

  private final UUID accountId;

  public WaiterCallDismissedEvent(UUID accountId, UUID restaurantId, UUID callId, long totalCount) {
    this.accountId = accountId;
    this.restaurantId = restaurantId;
    this.callId = callId;
    this.totalCount = totalCount;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public UUID getCallId() {
    return callId;
  }

  public long getTotalCount() {
    return totalCount;
  }
}

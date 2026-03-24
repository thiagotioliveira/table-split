package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class RestaurantCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final int numberOfTables;

  public RestaurantCreatedEvent(Restaurant restaurant, int numberOfTables) {
    this.accountId = restaurant.getAccountId();
    this.restaurantId = restaurant.getId();
    this.numberOfTables = numberOfTables;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public int getNumberOfTables() {
    return numberOfTables;
  }
}

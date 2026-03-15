package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class RestaurantCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;

  public RestaurantCreatedEvent(Restaurant restaurant) {
    this.accountId = restaurant.getAccountId();
    this.restaurantId = restaurant.getId();
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }
}

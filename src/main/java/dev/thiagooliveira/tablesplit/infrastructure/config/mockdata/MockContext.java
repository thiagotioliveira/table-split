package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.security.Context;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MockContext implements Context {

  private UUID restaurantId;

  @Override
  public UUID getRestaurantId() {
    return this.restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }
}

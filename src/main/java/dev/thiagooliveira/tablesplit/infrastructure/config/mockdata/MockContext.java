package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.security.Context;
import dev.thiagooliveira.tablesplit.domain.security.RestaurantContext;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MockContext implements Context {

  private RestaurantContext restaurantContext;

  public void initContext(
      UUID restaurantId, String restaurantName, String currency, List<Language> customerLanguages) {
    this.restaurantContext =
        new RestaurantContext(restaurantId, restaurantName, currency, customerLanguages);
  }

  @Override
  public RestaurantContext getRestaurant() {
    return this.restaurantContext;
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.security.Context;
import dev.thiagooliveira.tablesplit.domain.security.RestaurantContext;
import dev.thiagooliveira.tablesplit.domain.security.UserContext;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MockContext implements Context {
  private UUID accountId;
  private UserContext userContext;
  private RestaurantContext restaurantContext;

  public void initContext(
      UUID accountId,
      UUID userId,
      String userFirstName,
      String userLastname,
      String userEmail,
      UUID restaurantId,
      String restaurantName,
      String currency,
      List<Language> customerLanguages) {
    this.accountId = accountId;
    this.userContext = new UserContext(userId, userFirstName, userLastname, userEmail);
    this.restaurantContext =
        new RestaurantContext(restaurantId, restaurantName, currency, customerLanguages);
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  @Override
  public UserContext getUser() {
    return this.userContext;
  }

  @Override
  public RestaurantContext getRestaurant() {
    return this.restaurantContext;
  }
}

package dev.thiagooliveira.tablesplit.domain.security;

import java.util.UUID;

public interface Context {
  UUID getAccountId();

  UserContext getUser();

  RestaurantContext getRestaurant();
}

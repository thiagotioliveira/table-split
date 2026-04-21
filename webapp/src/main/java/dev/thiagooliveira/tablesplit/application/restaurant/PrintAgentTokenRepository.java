package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import java.util.Optional;
import java.util.UUID;

public interface PrintAgentTokenRepository {
  Optional<PrintAgentToken> findByRestaurantId(UUID restaurantId);

  Optional<PrintAgentToken> findByToken(String tokenValue);

  void save(PrintAgentToken token);
}

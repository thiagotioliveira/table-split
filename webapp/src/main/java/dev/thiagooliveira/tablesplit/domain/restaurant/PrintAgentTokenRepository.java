package dev.thiagooliveira.tablesplit.domain.restaurant;

import java.util.Optional;
import java.util.UUID;

public interface PrintAgentTokenRepository {
  Optional<PrintAgentToken> findByRestaurantId(UUID restaurantId);

  Optional<PrintAgentToken> findByToken(String tokenValue);

  void save(PrintAgentToken token);
}

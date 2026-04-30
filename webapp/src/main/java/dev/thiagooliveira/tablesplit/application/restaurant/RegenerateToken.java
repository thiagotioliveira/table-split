package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentTokenRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.UUID;

public class RegenerateToken {

  private final PrintAgentTokenRepository tokenRepository;
  private final RestaurantRepository restaurantRepository;

  public RegenerateToken(
      PrintAgentTokenRepository tokenRepository, RestaurantRepository restaurantRepository) {
    this.tokenRepository = tokenRepository;
    this.restaurantRepository = restaurantRepository;
  }

  public String execute(UUID restaurantId) {
    restaurantRepository
        .findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

    PrintAgentToken token =
        tokenRepository
            .findByRestaurantId(restaurantId)
            .orElseGet(() -> new PrintAgentToken(restaurantId, ""));

    token.setToken(PrintAgentToken.generateTokenValue());
    token.setCreatedAt(Time.now());
    token.setLastUsedAt(null);

    tokenRepository.save(token);
    return token.getToken();
  }
}

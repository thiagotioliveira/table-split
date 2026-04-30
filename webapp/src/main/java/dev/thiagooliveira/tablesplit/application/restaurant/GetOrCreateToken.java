package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentTokenRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.UUID;

public class GetOrCreateToken {

  private final PrintAgentTokenRepository tokenRepository;
  private final RestaurantRepository restaurantRepository;

  public GetOrCreateToken(
      PrintAgentTokenRepository tokenRepository, RestaurantRepository restaurantRepository) {
    this.tokenRepository = tokenRepository;
    this.restaurantRepository = restaurantRepository;
  }

  public String execute(UUID restaurantId) {
    return tokenRepository
        .findByRestaurantId(restaurantId)
        .map(PrintAgentToken::getToken)
        .orElseGet(
            () -> {
              restaurantRepository
                  .findById(restaurantId)
                  .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
              PrintAgentToken token =
                  new PrintAgentToken(restaurantId, PrintAgentToken.generateTokenValue());
              tokenRepository.save(token);
              return token.getToken();
            });
  }
}

package dev.thiagooliveira.tablesplit.application.printing;

import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.PrintAgentTokenEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.PrintAgentTokenJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrintAgentService {

  private final PrintAgentTokenJpaRepository tokenRepository;
  private final RestaurantJpaRepository restaurantRepository;

  public PrintAgentService(
      PrintAgentTokenJpaRepository tokenRepository, RestaurantJpaRepository restaurantRepository) {
    this.tokenRepository = tokenRepository;
    this.restaurantRepository = restaurantRepository;
  }

  @Transactional
  public String getOrCreateToken(UUID restaurantId) {
    return tokenRepository
        .findByRestaurantId(restaurantId)
        .map(PrintAgentTokenEntity::getToken)
        .orElseGet(
            () -> {
              RestaurantEntity restaurant =
                  restaurantRepository
                      .findById(restaurantId)
                      .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
              PrintAgentTokenEntity token =
                  new PrintAgentTokenEntity(restaurant, PrintAgentTokenEntity.generateTokenValue());
              return tokenRepository.save(token).getToken();
            });
  }

  @Transactional
  public String regenerateToken(UUID restaurantId) {
    RestaurantEntity restaurant =
        restaurantRepository
            .findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

    PrintAgentTokenEntity tokenEntity =
        tokenRepository
            .findByRestaurantId(restaurantId)
            .orElseGet(() -> new PrintAgentTokenEntity(restaurant, ""));

    tokenEntity.setToken(PrintAgentTokenEntity.generateTokenValue());
    tokenEntity.setCreatedAt(ZonedDateTime.now(Time.getZoneId()));
    tokenEntity.setLastUsedAt(null);

    return tokenRepository.save(tokenEntity).getToken();
  }

  @Transactional
  public PrintAgentTokenEntity validateAndUseToken(String tokenValue) {
    PrintAgentTokenEntity token =
        tokenRepository
            .findByToken(tokenValue)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    token.setLastUsedAt(ZonedDateTime.now(Time.getZoneId()));
    return tokenRepository.save(token);
  }
}

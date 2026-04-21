package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.application.restaurant.PrintAgentTokenRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PrintAgentTokenRepositoryAdapter implements PrintAgentTokenRepository {

  private final PrintAgentTokenJpaRepository tokenJpaRepository;

  public PrintAgentTokenRepositoryAdapter(PrintAgentTokenJpaRepository tokenJpaRepository) {
    this.tokenJpaRepository = tokenJpaRepository;
  }

  @Override
  public Optional<PrintAgentToken> findByRestaurantId(UUID restaurantId) {
    return tokenJpaRepository.findByRestaurantId(restaurantId).map(PrintAgentTokenEntity::toDomain);
  }

  @Override
  public Optional<PrintAgentToken> findByToken(String tokenValue) {
    return tokenJpaRepository.findByToken(tokenValue).map(PrintAgentTokenEntity::toDomain);
  }

  @Override
  public void save(PrintAgentToken token) {
    tokenJpaRepository.save(PrintAgentTokenEntity.fromDomain(token));
  }
}

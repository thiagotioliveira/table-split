package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentTokenRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PrintAgentTokenRepositoryAdapter implements PrintAgentTokenRepository {

  private final PrintAgentTokenJpaRepository tokenJpaRepository;
  private final PrintAgentTokenEntityMapper mapper;

  public PrintAgentTokenRepositoryAdapter(
      PrintAgentTokenJpaRepository tokenJpaRepository, PrintAgentTokenEntityMapper mapper) {
    this.tokenJpaRepository = tokenJpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<PrintAgentToken> findByRestaurantId(UUID restaurantId) {
    return tokenJpaRepository.findByRestaurantId(restaurantId).map(mapper::toDomain);
  }

  @Override
  public Optional<PrintAgentToken> findByToken(String tokenValue) {
    return tokenJpaRepository.findByToken(tokenValue).map(mapper::toDomain);
  }

  @Override
  public void save(PrintAgentToken token) {
    tokenJpaRepository.save(mapper.toEntity(token));
  }
}

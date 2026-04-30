package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentTokenRepository;

public class ValidateAndUseToken {

  private final PrintAgentTokenRepository tokenRepository;

  public ValidateAndUseToken(PrintAgentTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  public PrintAgentToken execute(String tokenValue) {
    PrintAgentToken token =
        tokenRepository
            .findByToken(tokenValue)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    token.setLastUsedAt(Time.now());
    tokenRepository.save(token);
    return token;
  }
}

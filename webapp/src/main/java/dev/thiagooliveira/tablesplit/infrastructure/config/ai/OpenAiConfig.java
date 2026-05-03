package dev.thiagooliveira.tablesplit.infrastructure.config.ai;

import dev.thiagooliveira.tablesplit.infrastructure.ai.AiClient;
import dev.thiagooliveira.tablesplit.infrastructure.ai.openai.OpenAiSimpleClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key", matchIfMissing = false)
public class OpenAiConfig {

  @Bean
  public AiClient aiClient(@Value("${spring.ai.openai.api-key:}") String apiKey) {
    return new OpenAiSimpleClient(apiKey);
  }
}

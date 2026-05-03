package dev.thiagooliveira.tablesplit.infrastructure.config.telegram;

import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ChatAiService;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.telegram.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${telegram.bot.token:}') "
        + "&& T(org.springframework.util.StringUtils).hasText('${telegram.bot.username:}')")
@ConditionalOnBean(ChatAiService.class)
public class TelegramConfig {

  private static final Logger logger = LoggerFactory.getLogger(TelegramConfig.class);

  @Bean
  public TelegramIdentityService telegramIdentityService(
      UserRepository userRepository,
      StaffRepository staffRepository,
      RestaurantRepository restaurantRepository) {
    return new TelegramIdentityService(userRepository, staffRepository, restaurantRepository);
  }

  @Bean
  public TelegramBot telegramBot(
      TelegramProperties properties,
      ChatAiService chatAiService,
      TelegramIdentityService identityService,
      TelegramUserMappingJpaRepository mappingRepository) {
    return new TelegramBot(properties, chatAiService, identityService, mappingRepository);
  }

  @Bean
  public TelegramNotificationListener telegramNotificationListener(
      TelegramBot telegramBot, TelegramUserMappingJpaRepository mappingRepository) {
    return new TelegramNotificationListener(telegramBot, mappingRepository);
  }
}

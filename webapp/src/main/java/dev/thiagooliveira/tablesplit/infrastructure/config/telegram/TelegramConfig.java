package dev.thiagooliveira.tablesplit.infrastructure.config.telegram;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.ai.AiClient;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.telegram.*;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ConditionalOnProperties({
  @ConditionalOnProperty(prefix = "telegram.bot", name = "token", matchIfMissing = false),
  @ConditionalOnProperty(prefix = "telegram.bot", name = "username", matchIfMissing = false)
})
@ConditionalOnBean(AiClient.class)
public class TelegramConfig {

  private static final Logger logger = LoggerFactory.getLogger(TelegramConfig.class);

  @Bean
  public BotContextService botContextService(
      GetRestaurant getRestaurant,
      GetCategory getCategory,
      GetItem getItem,
      TransactionalContext transactionalContext) {
    return new BotContextService(getRestaurant, getCategory, getItem, transactionalContext);
  }

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
      AiClient aiClient,
      TelegramIdentityService identityService,
      BotContextService botContextService,
      TelegramUserMappingJpaRepository mappingRepository) {
    return new TelegramBot(
        properties, aiClient, identityService, botContextService, mappingRepository);
  }

  @Bean
  public TelegramNotificationListener telegramNotificationListener(
      TelegramBot telegramBot, TelegramUserMappingJpaRepository mappingRepository) {
    return new TelegramNotificationListener(telegramBot, mappingRepository);
  }

  @Bean
  public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    try {
      botsApi.registerBot(telegramBot);
      logger.debug("Bot manually registered with TelegramBotsApi successfully!");
    } catch (TelegramApiException e) {
      logger.error("Error registering the bot manually: {}", e.getMessage());
    }
    return botsApi;
  }
}

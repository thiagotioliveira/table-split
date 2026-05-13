package dev.thiagooliveira.tablesplit.infrastructure.claudio.telegram.config;

import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.ClaudioService;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.persistence.TelegramUserMappingJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.telegram.*;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.telegram.api.TelegramWebhookController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${telegram.bot.token:}') "
        + "&& T(org.springframework.util.StringUtils).hasText('${telegram.bot.username:}')")
@ConditionalOnBean(ClaudioService.class)
public class TelegramConfig {

  private static final Logger logger = LoggerFactory.getLogger(TelegramConfig.class);

  @Bean
  public TelegramSender telegramSender(TelegramProperties properties) {
    return new TelegramSender(properties);
  }

  @Bean
  public TelegramUpdateHandler telegramUpdateHandler(
      ClaudioService claudioService,
      TelegramIdentityService identityService,
      TelegramUserMappingJpaRepository mappingRepository,
      TelegramSender telegramSender) {
    return new TelegramUpdateHandler(
        claudioService, identityService, mappingRepository, telegramSender);
  }

  @Bean
  @Profile("dev")
  public TelegramLongPollingBotImpl telegramBot(
      TelegramProperties properties, TelegramUpdateHandler updateHandler) {
    return new TelegramLongPollingBotImpl(properties, updateHandler);
  }

  @Bean
  @Profile("dev")
  public TelegramBotsApi telegramBotsApi(TelegramLongPollingBotImpl telegramLongPollingBotImpl)
      throws TelegramApiException {
    logger.debug("Setting up TelegramBotsApi for Polling mode (DEV)...");
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    try {
      botsApi.registerBot(telegramLongPollingBotImpl);
      logger.debug("Polling bot successfully registered!");
    } catch (TelegramApiException e) {
      logger.error("Error registering polling bot: {}", e.getMessage());
    }
    return botsApi;
  }

  @Bean
  @Profile("prod")
  public TelegramWebhookController telegramWebhookController(TelegramUpdateHandler updateHandler) {
    return new TelegramWebhookController(updateHandler);
  }

  @Bean
  @Profile("prod")
  public CommandLineRunner initWebhook(
      TelegramSender telegramSender, TelegramProperties properties) {
    return args -> {
      if (properties.getBaseUrl() == null || properties.getBaseUrl().isBlank()) {
        logger.warn("Base URL not configured. Webhook will not be registered.");
        return;
      }
      String webhookUrl = properties.getBaseUrl();
      if (!webhookUrl.endsWith("/")) webhookUrl += "/";
      webhookUrl +=
          properties.getWebhookPath().startsWith("/")
              ? properties.getWebhookPath().substring(1)
              : properties.getWebhookPath();

      logger.debug("Registering a Telegram Webhook: {}", webhookUrl);
      try {
        SetWebhook setWebhook = SetWebhook.builder().url(webhookUrl).build();
        telegramSender.execute(setWebhook);
        logger.info("Webhook successfully registered on Telegram!");
      } catch (TelegramApiException e) {
        logger.error("Error registering Webhook: {}", e.getMessage());
      }
    };
  }

  @Bean
  public TelegramNotificationListener telegramNotificationListener(
      TelegramSender telegramSender, TelegramUserMappingJpaRepository mappingRepository) {
    return new TelegramNotificationListener(telegramSender, mappingRepository);
  }

  @Bean
  public TelegramIdentityService telegramIdentityService(
      UserRepository userRepository,
      StaffRepository staffRepository,
      RestaurantRepository restaurantRepository) {
    return new TelegramIdentityService(userRepository, staffRepository, restaurantRepository);
  }
}

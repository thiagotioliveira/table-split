package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.infrastructure.config.telegram.TelegramProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramLongPollingBotImpl extends TelegramLongPollingBot {
  private static final Logger logger = LoggerFactory.getLogger(TelegramLongPollingBotImpl.class);

  private final TelegramProperties properties;
  private final TelegramUpdateHandler updateHandler;

  public TelegramLongPollingBotImpl(
      TelegramProperties properties, TelegramUpdateHandler updateHandler) {
    this.properties = properties;
    this.updateHandler = updateHandler;
    logger.info(
        "TelegramBot (Polling Mode) inicializado no profile DEV. Username: {}",
        properties.getUsername());
  }

  @Override
  public String getBotUsername() {
    return properties.getUsername();
  }

  @Override
  public String getBotToken() {
    return properties.getToken();
  }

  @Override
  public void onUpdateReceived(Update update) {
    updateHandler.onUpdateReceived(update);
  }
}

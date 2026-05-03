package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

  @Bean
  public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    try {
      botsApi.registerBot(telegramBot);
      System.out.println("Bot registrado manualmente no TelegramBotsApi com sucesso!");
    } catch (TelegramApiException e) {
      System.err.println("Erro ao registrar o bot manualmente: " + e.getMessage());
    }
    return botsApi;
  }
}

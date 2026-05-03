package dev.thiagooliveira.tablesplit.infrastructure.web.telegram;

import dev.thiagooliveira.tablesplit.infrastructure.telegram.TelegramUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@ConditionalOnBean(TelegramUpdateHandler.class)
public class TelegramWebhookController {
  private static final Logger logger = LoggerFactory.getLogger(TelegramWebhookController.class);
  private final TelegramUpdateHandler updateHandler;

  public TelegramWebhookController(TelegramUpdateHandler updateHandler) {
    this.updateHandler = updateHandler;
    logger.info("TelegramWebhookController inicializado (Modo Produção).");
  }

  @PostMapping("/telegram/webhook")
  public void onUpdateReceived(@RequestBody Update update) {
    logger.debug("Webhook do Telegram recebeu um update.");
    updateHandler.onUpdateReceived(update);
  }
}

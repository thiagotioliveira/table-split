package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingJpaRepository;
import java.util.List;
import org.springframework.context.event.EventListener;

public class TelegramNotificationListener {

  private final TelegramBot telegramBot;
  private final TelegramUserMappingJpaRepository mappingRepository;

  public TelegramNotificationListener(
      TelegramBot telegramBot, TelegramUserMappingJpaRepository mappingRepository) {
    this.telegramBot = telegramBot;
    this.mappingRepository = mappingRepository;
  }

  @EventListener
  public void onTicketCreated(TicketCreatedEvent event) {
    List<TelegramUserMappingEntity> mappings =
        mappingRepository.findByRestaurantId(event.getRestaurantId());

    String message =
        String.format(
            "📦 *NOVO PEDIDO!*\n\n" + "📍 *Mesa:* %s\n" + "🛒 *Itens:* %d\n" + "💰 *Total:* €%.2f",
            event.getTableCod(),
            event.getTicket().getItems().size(),
            event.getTicket().calculateTotal());

    for (TelegramUserMappingEntity mapping : mappings) {
      telegramBot.sendTyping(mapping.getChatId());
      telegramBot.sendTextWithButton(
          mapping.getChatId(),
          message,
          "✅ Confirmar Recebimento",
          "confirm_order:" + event.getOrderId());
    }
  }
}

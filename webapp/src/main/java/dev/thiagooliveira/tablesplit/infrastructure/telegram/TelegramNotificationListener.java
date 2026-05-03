package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingJpaRepository;

public class TelegramNotificationListener {

  private final TelegramSender telegramSender;
  private final TelegramUserMappingJpaRepository mappingRepository;

  public TelegramNotificationListener(
      TelegramSender telegramSender, TelegramUserMappingJpaRepository mappingRepository) {
    this.telegramSender = telegramSender;
    this.mappingRepository = mappingRepository;
  }

  // @EventListener
  public void onTicketCreated(TicketCreatedEvent event) {
    /* Desativado temporariamente para testes
    List<TelegramUserMappingEntity> mappings =
        mappingRepository.findByRestaurantId(event.getRestaurantId());

    String message =
        String.format(
            "📦 *NOVO PEDIDO!*\n\n" + "📍 *Mesa:* %s\n" + "🛒 *Itens:* %d\n" + "💰 *Total:* €%.2f",
            event.getTableCod(),
            event.getTicket().getItems().size(),
            event.getTicket().calculateTotal());

    for (TelegramUserMappingEntity mapping : mappings) {
      telegramSender.sendTyping(mapping.getChatId());
      telegramSender.sendTextWithButton(
          mapping.getChatId(),
          message,
          "✅ Confirmar Recebimento",
          "confirm_order:" + event.getOrderId());
    }
    */
  }
}

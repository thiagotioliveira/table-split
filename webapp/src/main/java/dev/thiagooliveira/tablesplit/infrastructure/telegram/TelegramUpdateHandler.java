package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.infrastructure.ai.ChatAiService;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram.TelegramUserMappingJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUpdateHandler {
  private static final Logger logger = LoggerFactory.getLogger(TelegramUpdateHandler.class);

  private final ChatAiService chatAiService;
  private final TelegramIdentityService identityService;
  private final TelegramUserMappingJpaRepository mappingRepository;
  private final TelegramSender telegramSender;

  private enum BotState {
    IDLE,
    AWAITING_CONTACT,
    AWAITING_SLUG
  }

  private final Map<Long, String> chatToPhoneMap = new ConcurrentHashMap<>();
  private final Map<Long, BotState> userStates = new ConcurrentHashMap<>();
  private final Map<Long, TelegramIdentityService.IdentifiedUser> identifiedUsers =
      new ConcurrentHashMap<>();

  public TelegramUpdateHandler(
      ChatAiService chatAiService,
      TelegramIdentityService identityService,
      TelegramUserMappingJpaRepository mappingRepository,
      TelegramSender telegramSender) {
    this.chatAiService = chatAiService;
    this.identityService = identityService;
    this.mappingRepository = mappingRepository;
    this.telegramSender = telegramSender;
  }

  public void onUpdateReceived(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      Long chatId = message.getChatId();

      if (message.hasText()) {
        String text = message.getText();
        logger.info("Mensagem recebida de chatId: {} Texto: {}", chatId, text);

        if ("/start".equals(text)) {
          requestContact(chatId);
        } else {
          handleTextMessage(chatId, text);
        }
      } else if (message.hasContact()) {
        handleContact(chatId, message.getContact());
      }
    } else if (update.hasCallbackQuery()) {
      handleCallbackQuery(update.getCallbackQuery());
    }
  }

  private void handleCallbackQuery(CallbackQuery callbackQuery) {
    String data = callbackQuery.getData();
    Long chatId = callbackQuery.getMessage().getChatId();

    if (data.startsWith("confirm_order:")) {
      telegramSender.sendText(chatId, "✅ Você confirmou o recebimento do pedido!");
    }
  }

  private void requestContact(Long chatId) {
    userStates.put(chatId, BotState.AWAITING_CONTACT);
    telegramSender.sendText(
        chatId,
        "Olá! Para começar, por favor compartilhe seu contato para que eu possa identificar você no Table Split.");
  }

  private void handleContact(Long chatId, Contact contact) {
    String phone = contact.getPhoneNumber();
    if (!phone.startsWith("+")) phone = "+" + phone;
    chatToPhoneMap.put(chatId, phone);

    Optional<TelegramIdentityService.IdentifiedUser> userOpt = identityService.identify(phone);
    if (userOpt.isPresent()) {
      var user = userOpt.get();
      identifiedUsers.put(chatId, user);
      userStates.put(chatId, BotState.IDLE);
      saveMapping(chatId, phone, user);
      telegramSender.sendText(
          chatId,
          "Bem-vindo, "
              + user.name()
              + "! Você foi identificado como "
              + user.role()
              + ". Como posso ajudar hoje?");
      return;
    }

    userStates.put(chatId, BotState.AWAITING_SLUG);
    telegramSender.sendText(
        chatId,
        "Não encontrei você como cliente. Você faz parte da equipe de algum restaurante? Se sim, por favor digite o identificador (slug) do seu restaurante.");
  }

  private void saveMapping(Long chatId, String phone, TelegramIdentityService.IdentifiedUser user) {
    if (user.restaurantId() == null) return;
    TelegramUserMappingEntity entity = new TelegramUserMappingEntity();
    entity.setChatId(chatId);
    entity.setPhone(phone);
    entity.setRestaurantId(user.restaurantId());
    entity.setName(user.name());
    entity.setRole(user.role());
    mappingRepository.save(entity);
  }

  private void handleTextMessage(Long chatId, String text) {
    BotState state = userStates.getOrDefault(chatId, BotState.IDLE);
    String phone = chatToPhoneMap.get(chatId);

    if (phone == null) {
      requestContact(chatId);
      return;
    }

    if (state == BotState.AWAITING_SLUG) {
      Optional<TelegramIdentityService.IdentifiedUser> staffOpt =
          identityService.identifyStaffBySlug(text.trim().toLowerCase(), phone);
      if (staffOpt.isPresent()) {
        var staff = staffOpt.get();
        identifiedUsers.put(chatId, staff);
        userStates.put(chatId, BotState.IDLE);
        saveMapping(chatId, phone, staff);
        telegramSender.sendText(
            chatId,
            "Excelente! Bem-vindo, "
                + staff.name()
                + "! Agora você está identificado como "
                + staff.role()
                + ". Como posso ajudar?");
      } else {
        telegramSender.sendText(
            chatId,
            "Desculpe, não encontrei o restaurante com o identificador '"
                + text
                + "' ou você não está cadastrado na equipe.");
      }
      return;
    }

    var user = identifiedUsers.get(chatId);
    if (user == null || user.restaurantId() == null) {
      telegramSender.sendText(
          chatId, "Desculpe, não consegui identificar seu restaurante para fornecer informações.");
      return;
    }

    telegramSender.sendTyping(chatId);

    try {
      TenantContext.setCurrentTenant(TenantContext.generateTenantIdentifier(user.restaurantId()));
      String response = chatAiService.chat(chatId, text);
      telegramSender.sendText(chatId, response);
    } catch (Exception e) {
      telegramSender.sendText(
          chatId, "Desculpe, tive um problema ao processar sua mensagem com a IA.");
      logger.error("Error in AI chat", e);
    } finally {
      TenantContext.clear();
    }
  }
}

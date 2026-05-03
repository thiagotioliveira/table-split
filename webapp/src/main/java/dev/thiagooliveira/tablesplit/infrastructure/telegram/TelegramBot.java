package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.infrastructure.ai.ChatAiService;
import dev.thiagooliveira.tablesplit.infrastructure.config.telegram.TelegramProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class TelegramBot extends TelegramLongPollingBot {
  private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

  private final TelegramProperties properties;
  private final TelegramIdentityService identityService;
  private final ChatAiService chatAiService;
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram
          .TelegramUserMappingJpaRepository
      mappingRepository;

  // States for the conversation
  private enum BotState {
    IDLE,
    AWAITING_CONTACT,
    AWAITING_SLUG
  }

  private final Map<Long, String> chatToPhoneMap = new ConcurrentHashMap<>();
  private final Map<Long, BotState> userStates = new ConcurrentHashMap<>();
  private final Map<Long, TelegramIdentityService.IdentifiedUser> identifiedUsers =
      new ConcurrentHashMap<>();

  public TelegramBot(
      TelegramProperties properties,
      ChatAiService chatAiService,
      TelegramIdentityService identityService,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram
              .TelegramUserMappingJpaRepository
          mappingRepository) {
    this.properties = properties;
    this.chatAiService = chatAiService;
    this.identityService = identityService;
    this.mappingRepository = mappingRepository;
    logger.debug("TelegramBot inicializado com sucesso! Username: {}", properties.getUsername());
  }

  @Override
  public String getBotUsername() {
    logger.debug("SDK solicitou o username: {}", properties.getUsername());
    return properties.getUsername();
  }

  @Override
  public String getBotToken() {
    logger.debug("SDK solicitou o token.");
    return properties.getToken();
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      Long chatId = message.getChatId();

      if (message.hasText()) {
        String text = message.getText();
        logger.debug("Mensagem recebida de chatId: {} Texto: {}", chatId, text);

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
      sendText(chatId, "✅ Você confirmou o recebimento do pedido!");
    }
  }

  public void sendTextWithButton(Long chatId, String text, String buttonText, String callbackData) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId.toString());
    sendMessage.setText(text);
    sendMessage.setParseMode("Markdown");

    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    List<InlineKeyboardButton> rowInline = new ArrayList<>();

    InlineKeyboardButton button = new InlineKeyboardButton();
    button.setText(buttonText);
    button.setCallbackData(callbackData);

    rowInline.add(button);
    rowsInline.add(rowInline);
    markupInline.setKeyboard(rowsInline);
    sendMessage.setReplyMarkup(markupInline);

    try {
      execute(sendMessage);
    } catch (Exception e) {
      logger.error("Error sending message with button", e);
    }
  }

  private void requestContact(Long chatId) {
    logger.debug("Solicitando contato para chatId: {}", chatId);
    userStates.put(chatId, BotState.AWAITING_CONTACT);
    SendMessage sendMessage = new SendMessage();

    sendMessage.setChatId(chatId.toString());
    sendMessage.setText(
        "Olá! Para começar, por favor compartilhe seu contato para que eu possa identificar você no Table Split.");

    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setResizeKeyboard(true);
    keyboardMarkup.setOneTimeKeyboard(true);

    KeyboardButton contactButton = new KeyboardButton("Compartilhar Contato");
    contactButton.setRequestContact(true);

    KeyboardRow row = new KeyboardRow();
    row.add(contactButton);
    keyboardMarkup.setKeyboard(Collections.singletonList(row));

    sendMessage.setReplyMarkup(keyboardMarkup);

    try {
      execute(sendMessage);
    } catch (Exception e) {
      logger.error("Error requesting contact", e);
    }
  }

  private void handleContact(Long chatId, Contact contact) {
    String phone = contact.getPhoneNumber();
    if (!phone.startsWith("+")) {
      phone = "+" + phone;
    }

    chatToPhoneMap.put(chatId, phone);

    Optional<TelegramIdentityService.IdentifiedUser> userOpt = identityService.identify(phone);
    if (userOpt.isPresent()) {
      var user = userOpt.get();
      identifiedUsers.put(chatId, user);
      userStates.put(chatId, BotState.IDLE);
      saveMapping(chatId, phone, user);
      sendText(
          chatId,
          "Bem-vindo, "
              + user.name()
              + "! Você foi identificado como "
              + user.role()
              + ". Como posso ajudar hoje?");
      return;
    }

    // Not found in Users, ask for slug
    userStates.put(chatId, BotState.AWAITING_SLUG);
    sendText(
        chatId,
        "Não encontrei você como cliente. Você faz parte da equipe de algum restaurante? Se sim, por favor digite o identificador (slug) do seu restaurante.");
  }

  private void saveMapping(Long chatId, String phone, TelegramIdentityService.IdentifiedUser user) {
    if (user.restaurantId() == null) return;

    var entity =
        new dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram
            .TelegramUserMappingEntity();
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
        sendText(
            chatId,
            "Excelente! Bem-vindo, "
                + staff.name()
                + "! Agora você está identificado como "
                + staff.role()
                + ". Como posso ajudar?");
      } else {
        sendText(
            chatId,
            "Desculpe, não encontrei o restaurante com o identificador '"
                + text
                + "' ou você não está cadastrado na equipe. Verifique o identificador ou entre em contato com o administrador.");
      }
      return;
    }

    // Regular AI Chat
    var user = identifiedUsers.get(chatId);
    if (user == null || user.restaurantId() == null) {
      sendText(
          chatId, "Desculpe, não consegui identificar seu restaurante para fornecer informações.");
      return;
    }

    sendTyping(chatId);

    try {
      dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.setCurrentTenant(
          dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext
              .generateTenantIdentifier(user.restaurantId()));

      String response = chatAiService.chat(chatId, text);
      sendText(chatId, response);
    } catch (Exception e) {
      sendText(chatId, "Desculpe, tive um problema ao processar sua mensagem com a IA.");
      logger.error("Error in regular AI chat", e);
    } finally {
      dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.clear();
    }
  }

  public void sendTyping(Long chatId) {
    SendChatAction action = new SendChatAction();
    action.setChatId(chatId.toString());
    action.setAction(org.telegram.telegrambots.meta.api.methods.ActionType.TYPING);
    try {
      execute(action);
    } catch (Exception e) {
      logger.error("Error sending typing action", e);
    }
  }

  public void sendText(Long chatId, String text) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId.toString());
    sendMessage.setText(text);
    sendMessage.setParseMode("Markdown");
    sendMessage.setDisableNotification(false);
    try {
      execute(sendMessage);
    } catch (Exception e) {
      logger.error("Error sending text message", e);
    }
  }
}

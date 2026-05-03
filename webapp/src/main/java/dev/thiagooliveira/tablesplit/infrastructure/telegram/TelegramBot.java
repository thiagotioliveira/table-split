package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.infrastructure.ai.openai.OpenAiSimpleClient;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class TelegramBot extends TelegramLongPollingBot {

  private final TelegramProperties properties;
  private final OpenAiSimpleClient aiClient;
  private final TelegramIdentityService identityService;
  private final BotContextService botContextService;

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
      OpenAiSimpleClient aiClient,
      TelegramIdentityService identityService,
      BotContextService botContextService) {
    this.properties = properties;
    this.aiClient = aiClient;
    this.identityService = identityService;
    this.botContextService = botContextService;
    System.out.println(
        "TelegramBot inicializado com sucesso! Username: " + properties.getUsername());
  }

  @Override
  public String getBotUsername() {
    System.out.println("SDK solicitou o username: " + properties.getUsername());
    return properties.getUsername();
  }

  @Override
  public String getBotToken() {
    System.out.println("SDK solicitou o token.");
    return properties.getToken();
  }

  @Override
  public void onUpdateReceived(Update update) {
    System.out.println("Recebi uma atualização do Telegram!");
    if (update.hasMessage()) {
      var message = update.getMessage();
      Long chatId = message.getChatId();
      System.out.println("Mensagem recebida de chatId: " + chatId + " Texto: " + message.getText());

      if (message.hasText() && "/start".equals(message.getText())) {
        System.out.println("Comando /start detectado.");
        requestContact(chatId);
        return;
      }

      if (message.hasContact()) {
        handleContact(chatId, message.getContact());
        return;
      }

      if (message.hasText()) {
        handleTextMessage(chatId, message.getText());
      }
    }
  }

  private void requestContact(Long chatId) {
    System.out.println("Solicitando contato para chatId: " + chatId);
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
      e.printStackTrace();
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
    String userName = (user != null) ? user.name() : "Usuário";
    String role = (user != null) ? user.role() : "Desconhecido";

    String restaurantContext = "";
    if (user != null && user.restaurantId() != null) {
      restaurantContext = botContextService.getRestaurantContext(user.restaurantId());
    }

    String systemPrompt =
        String.format(
            "Você é o assistente inteligente do Table Split. Você está conversando com %s, que é um %s.\n"
                + "Seja prestativo, profissional e use um tom amigável.\n"
                + "Abaixo estão informações sobre o restaurante para você usar nas respostas:\n%s",
            userName, role, restaurantContext);

    try {
      String response = aiClient.chat(systemPrompt, text);
      sendText(chatId, response);
    } catch (Exception e) {
      sendText(chatId, "Desculpe, tive um problema ao processar sua mensagem com a IA.");
      e.printStackTrace();
    }
  }

  private void sendText(Long chatId, String text) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId.toString());
    sendMessage.setText(text);
    try {
      execute(sendMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

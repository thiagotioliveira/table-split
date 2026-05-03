package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.infrastructure.config.telegram.TelegramProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramSender extends DefaultAbsSender {
  private static final Logger logger = LoggerFactory.getLogger(TelegramSender.class);
  private final TelegramProperties properties;

  public TelegramSender(TelegramProperties properties) {
    super(new DefaultBotOptions());
    this.properties = properties;
  }

  @Override
  public String getBotToken() {
    return properties.getToken();
  }

  public <T extends Serializable, Method extends BotApiMethod<T>> T executeSafe(Method method) {
    try {
      return execute(method);
    } catch (TelegramApiException e) {
      logger.error("Erro ao enviar mensagem para o Telegram: {}", e.getMessage());
      return null;
    }
  }

  public void sendText(Long chatId, String text) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId.toString());
    sendMessage.setText(text);
    sendMessage.setParseMode("Markdown");
    executeSafe(sendMessage);
  }

  public void sendTyping(Long chatId) {
    SendChatAction action = new SendChatAction();
    action.setChatId(chatId.toString());
    action.setAction(ActionType.TYPING);
    executeSafe(action);
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

    executeSafe(sendMessage);
  }
}

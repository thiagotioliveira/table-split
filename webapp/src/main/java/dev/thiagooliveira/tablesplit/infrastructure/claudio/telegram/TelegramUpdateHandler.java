package dev.thiagooliveira.tablesplit.infrastructure.claudio.telegram;

import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.ClaudioService;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.LanguageDetector;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.persistence.TelegramUserMappingEntity;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.persistence.TelegramUserMappingJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUpdateHandler {
  private static final Logger logger = LoggerFactory.getLogger(TelegramUpdateHandler.class);

  private final ClaudioService claudioService;
  private final TelegramIdentityService identityService;
  private final TelegramUserMappingJpaRepository mappingRepository;
  private final TelegramSender telegramSender;
  private final MessageSource messageSource;
  private final LanguageDetector languageDetector;
  private final RestaurantRepository restaurantRepository;
  private final AccountRepository accountRepository;

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
      ClaudioService claudioService,
      TelegramIdentityService identityService,
      TelegramUserMappingJpaRepository mappingRepository,
      TelegramSender telegramSender,
      MessageSource messageSource,
      LanguageDetector languageDetector,
      RestaurantRepository restaurantRepository,
      AccountRepository accountRepository) {
    this.claudioService = claudioService;
    this.identityService = identityService;
    this.mappingRepository = mappingRepository;
    this.telegramSender = telegramSender;
    this.messageSource = messageSource;
    this.languageDetector = languageDetector;
    this.restaurantRepository = restaurantRepository;
    this.accountRepository = accountRepository;
  }

  public void onUpdateReceived(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      Long chatId = message.getChatId();

      if (message.hasText()) {
        String text = message.getText();
        logger.debug("Mensagem recebida de chatId: {} Texto: {}", chatId, text);

        if ("/start".equals(text)) {
          requestContact(chatId);
        } else if ("/reset".equals(text)) {
          handleReset(chatId);
        } else if (text.startsWith("/login ")) {
          String slug = text.substring(7).trim().toLowerCase();
          handleSlugLogin(chatId, slug);
        } else {
          detectLanguage(chatId, text);
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
      telegramSender.sendText(chatId, getMessage("telegram.confirm.order", chatId));
    }
  }

  private void requestContact(Long chatId) {
    userStates.put(chatId, BotState.AWAITING_CONTACT);
    telegramSender.sendContactRequest(
        chatId, getMessage("telegram.welcome.request.contact", chatId));
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
          chatId, getMessage("telegram.welcome.identified", chatId, user.name(), user.role()));
      return;
    }

    userStates.put(chatId, BotState.AWAITING_SLUG);
    telegramSender.sendText(chatId, getMessage("telegram.login.staff.slug.request", chatId));
  }

  @Transactional
  public void handleReset(Long chatId) {
    identifiedUsers.remove(chatId);
    userStates.remove(chatId);
    mappingRepository.deleteByChatId(chatId);
    chatLocales.remove(chatId);
    telegramSender.sendText(chatId, getMessage("telegram.reset.success", chatId));
  }

  @Transactional
  public void handleSlugLogin(Long chatId, String slug) {
    String phone = chatToPhoneMap.get(chatId);
    if (phone == null) {
      // Try to load phone from DB
      TelegramUserMappingEntity mapping = mappingRepository.findById(chatId).orElse(null);
      if (mapping != null) {
        phone = mapping.getPhone();
        chatToPhoneMap.put(chatId, phone);
      }
    }

    if (phone == null) {
      telegramSender.sendText(chatId, getMessage("telegram.login.request.start", chatId));
      return;
    }

    Optional<TelegramIdentityService.IdentifiedUser> staffOpt =
        identityService.identifyStaffBySlug(slug, phone);
    if (staffOpt.isPresent()) {
      var staff = staffOpt.get();
      identifiedUsers.put(chatId, staff);
      telegramSender.sendText(
          chatId, getMessage("telegram.welcome.identified", chatId, staff.name(), staff.role()));
    } else {
      telegramSender.sendText(chatId, getMessage("telegram.login.staff.not_found", chatId, slug));
    }
  }

  private TelegramIdentityService.IdentifiedUser loadFromDb(Long chatId) {
    return mappingRepository
        .findById(chatId)
        .map(
            m -> {
              var user =
                  new TelegramIdentityService.IdentifiedUser(
                      m.getName(), m.getRole(), m.getRestaurantId());
              identifiedUsers.put(chatId, user);
              chatToPhoneMap.put(chatId, m.getPhone());
              return user;
            })
        .orElse(null);
  }

  private void saveMapping(Long chatId, String phone, TelegramIdentityService.IdentifiedUser user) {
    if (user.restaurantId() == null) return;
    TelegramUserMappingEntity entity = new TelegramUserMappingEntity();
    entity.setChatId(chatId);
    entity.setPhone(phone);
    entity.setRestaurantId(user.restaurantId());
    entity.setName(user.name());
    mappingRepository.save(entity);
    chatLocales.put(chatId, getLocale(chatId)); // Ensure it's in sync
  }

  private void handleTextMessage(Long chatId, String text) {
    BotState state = userStates.getOrDefault(chatId, BotState.IDLE);
    String phone = chatToPhoneMap.get(chatId);

    if (phone == null) {
      // Tenta carregar do banco de dados se não estiver em memória
      loadFromDb(chatId);
      phone = chatToPhoneMap.get(chatId);
    }

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
            chatId, getMessage("telegram.login.staff.success", chatId, staff.name(), staff.role()));
      } else {
        telegramSender.sendText(chatId, getMessage("telegram.login.staff.not_found", chatId, text));
      }
      return;
    }

    var user = identifiedUsers.get(chatId);
    if (user == null) {
      user = loadFromDb(chatId);
    }

    if (user == null || user.restaurantId() == null) {
      telegramSender.sendText(chatId, getMessage("telegram.error.no_restaurant", chatId));
      return;
    }

    var restaurantOpt = restaurantRepository.findById(user.restaurantId());
    if (restaurantOpt.isEmpty()) {
      telegramSender.sendText(chatId, getMessage("telegram.error.no_restaurant", chatId));
      return;
    }

    var accountOpt = accountRepository.findById(restaurantOpt.get().getAccountId());
    if (accountOpt.isEmpty()) {
      telegramSender.sendText(chatId, getMessage("telegram.error.no_restaurant", chatId));
      return;
    }

    var account = accountOpt.get();
    if (account.getEffectivePlan() != dev.thiagooliveira.tablesplit.domain.account.Plan.PROFESSIONAL
        || !account.isActive()) {
      telegramSender.sendText(chatId, getMessage("telegram.error.no_restaurant", chatId));
      return;
    }

    telegramSender.sendTyping(chatId);

    try {
      TenantContext.setCurrentTenant(TenantContext.generateTenantIdentifier(user.restaurantId()));
      String response = claudioService.chat(chatId, text);
      telegramSender.sendText(chatId, response);
    } catch (Exception e) {
      telegramSender.sendText(chatId, getMessage("telegram.error.ai", chatId));
      logger.error("Error in AI chat", e);
    } finally {
      TenantContext.clear();
    }
  }

  private final Map<Long, Locale> chatLocales = new ConcurrentHashMap<>();

  private Locale getLocale(Long chatId) {
    return chatLocales.computeIfAbsent(
        chatId,
        id ->
            mappingRepository
                .findById(id)
                .map(m -> Locale.forLanguageTag(m.getLanguage()))
                .orElse(Locale.forLanguageTag("PT")));
  }

  private void detectLanguage(Long chatId, String text) {
    if (text == null || text.startsWith("/") || text.length() < 3) return;

    try {
      String detected = languageDetector.detect(text);
      if (detected != null && (detected.equals("PT") || detected.equals("EN"))) {
        Locale locale = Locale.forLanguageTag(detected);
        chatLocales.put(chatId, locale);

        mappingRepository
            .findById(chatId)
            .ifPresent(
                mapping -> {
                  mapping.setLanguage(detected);
                  mappingRepository.save(mapping);
                });
        logger.debug("Language detected for chatId {}: {}", chatId, detected);
      }
    } catch (Exception e) {
      logger.warn("Failed to detect language for chatId {}: {}", chatId, e.getMessage());
    }
  }

  private String getMessage(String key, Long chatId, Object... args) {
    return messageSource.getMessage(key, args, getLocale(chatId));
  }
}

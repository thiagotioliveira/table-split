package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

public class Broadcaster {

  private static final Logger logger = LoggerFactory.getLogger(Broadcaster.class);

  private final PushSubscriptionRepository repository;
  private final PushSender sender;
  private final MessageSource messageSource;

  public Broadcaster(
      PushSubscriptionRepository repository, PushSender sender, MessageSource messageSource) {
    this.repository = repository;
    this.sender = sender;
    this.messageSource = messageSource;
  }

  public void newOrder(UUID restaurantId, String tableCod, String customerName, UUID excludeId) {
    broadcast(
        restaurantId,
        excludeId,
        PushSubscription::isNotifyNewOrders,
        lang -> {
          String title =
              messageSource.getMessage(
                  "notification.push.new_order.title", new Object[] {tableCod}, getLocale(lang));
          String body =
              messageSource.getMessage(
                  "notification.push.new_order.body", new Object[] {customerName}, getLocale(lang));
          return formatPayload(title, body, "/orders");
        });
  }

  public void callWaiter(UUID restaurantId, String tableCod) {
    broadcast(
        restaurantId,
        null,
        PushSubscription::isNotifyCallWaiter,
        lang -> {
          String title =
              messageSource.getMessage(
                  "notification.push.waiter_call.title", new Object[] {tableCod}, getLocale(lang));
          String body =
              messageSource.getMessage(
                  "notification.push.waiter_call.body", new Object[] {tableCod}, getLocale(lang));
          return formatPayload(title, body, "/orders");
        });
  }

  public void orderClosed(
      UUID restaurantId,
      String tableCod,
      java.math.BigDecimal amount,
      dev.thiagooliveira.tablesplit.domain.common.Currency currency,
      UUID excludeId) {
    broadcast(
        restaurantId,
        excludeId,
        PushSubscription::isNotifyOrderClosed,
        lang -> {
          String formattedAmount = currency.format(amount, lang);
          String title;
          String body;
          if (tableCod != null) {
            title =
                messageSource.getMessage(
                    "notification.push.order_closed.table.title",
                    new Object[] {tableCod},
                    getLocale(lang));
            body =
                messageSource.getMessage(
                    "notification.push.order_closed.table.body",
                    new Object[] {tableCod, formattedAmount},
                    getLocale(lang));
          } else {
            title =
                messageSource.getMessage(
                    "notification.push.order_closed.counter.title", null, getLocale(lang));
            body =
                messageSource.getMessage(
                    "notification.push.order_closed.counter.body",
                    new Object[] {formattedAmount},
                    getLocale(lang));
          }
          return formatPayload(title, body, "/tables");
        });
  }

  public void general(UUID restaurantId, String titleKey, Object[] args, String url) {
    general(restaurantId, titleKey, args, url, null);
  }

  public void general(
      UUID restaurantId, String titleKey, Object[] args, String url, UUID excludeId) {
    broadcast(
        restaurantId,
        excludeId,
        sub -> true,
        lang -> {
          String title = messageSource.getMessage(titleKey, args, getLocale(lang));
          return formatPayload(title, "", url);
        });
  }

  private void broadcast(
      UUID restaurantId,
      UUID excludeId,
      Predicate<PushSubscription> filter,
      java.util.function.Function<Language, String> payloadGenerator) {
    List<PushSubscription> subscriptions = repository.findAllByRestaurantId(restaurantId);
    Predicate<PushSubscription> finalFilter =
        sub -> {
          if (excludeId != null) {
            if (excludeId.equals(sub.getStaffId()) || excludeId.equals(sub.getUserId())) {
              return false;
            }
          }
          return filter.test(sub);
        };

    subscriptions.stream()
        .filter(finalFilter)
        .forEach(
            sub -> {
              Language lang = sub.getLanguage() != null ? sub.getLanguage() : Language.PT;
              String payload = payloadGenerator.apply(lang);
              sender.send(sub, payload);
            });
  }

  private String formatPayload(String title, String body, String url) {
    return String.format(
        "{\"title\": \"%s\", \"body\": \"%s\", \"url\": \"%s\"}", title, body, url);
  }

  private Locale getLocale(Language language) {
    return switch (language) {
      case PT -> new Locale("pt", "PT");
      case EN -> Locale.UK;
      default -> Locale.UK;
    };
  }
}

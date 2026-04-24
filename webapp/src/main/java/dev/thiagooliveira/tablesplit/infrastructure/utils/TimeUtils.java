package dev.thiagooliveira.tablesplit.infrastructure.utils;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  public static String format(ZonedDateTime dateTime) {
    if (dateTime == null) return "";
    return dateTime.format(FORMATTER);
  }

  private static String timeAgo(ZonedDateTime dateTime) {
    if (dateTime == null) return "";

    Duration duration = Duration.between(dateTime, Time.now());
    long minutes = duration.toMinutes();
    long hours = duration.toHours();
    long days = duration.toDays();

    if (days > 0) {
      return "há " + days + " d";
    } else if (hours > 0) {
      return "há " + hours + " h";
    } else if (minutes > 0) {
      return "há " + minutes + " min";
    } else {
      return "agora";
    }
  }

  public static String timeAgo(
      ZonedDateTime dateTime,
      org.springframework.context.MessageSource messageSource,
      Language language) {
    if (dateTime == null) return "";
    if (messageSource == null) return timeAgo(dateTime);

    Duration duration = Duration.between(dateTime, Time.now());
    long minutes = duration.toMinutes();
    long hours = duration.toHours();
    long days = duration.toDays();

    java.util.Locale locale =
        language != null
            ? java.util.Locale.forLanguageTag(language.name().toLowerCase())
            : org.springframework.context.i18n.LocaleContextHolder.getLocale();

    if (days > 0) {
      return messageSource.getMessage("time.ago.days", new Object[] {days}, locale);
    } else if (hours > 0) {
      return messageSource.getMessage("time.ago.hours", new Object[] {hours}, locale);
    } else if (minutes > 0) {
      return messageSource.getMessage("time.ago.minutes", new Object[] {minutes}, locale);
    } else {
      return messageSource.getMessage("time.ago.now", null, locale);
    }
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.utils;

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

  public static String timeAgo(ZonedDateTime dateTime) {
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
}

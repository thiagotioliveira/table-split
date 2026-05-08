package dev.thiagooliveira.tablesplit.infrastructure.timezone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Time {

  private static ZoneId zoneId;

  public Time(@Value("${app.time.zone-id}") String zoneId) {
    Time.zoneId = ZoneId.of(zoneId);
    dev.thiagooliveira.tablesplit.domain.common.Time.setZoneId(Time.zoneId);
  }

  public static ZoneId getZoneId() {
    return zoneId;
  }

  public static ZonedDateTime nowZonedDateTime() {
    return ZonedDateTime.now(zoneId);
  }

  public static LocalDateTime nowLocalDateTime() {
    return LocalDateTime.now(zoneId);
  }

  public static LocalDate nowLocalDate() {
    return LocalDate.now(zoneId);
  }
}

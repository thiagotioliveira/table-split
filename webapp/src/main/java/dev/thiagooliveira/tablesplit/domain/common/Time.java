package dev.thiagooliveira.tablesplit.domain.common;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Time {
  private static ZoneId zoneId = ZoneId.systemDefault();

  public static void setZoneId(ZoneId zoneId) {
    Time.zoneId = zoneId;
  }

  public static ZonedDateTime now() {
    return ZonedDateTime.now(zoneId);
  }
}

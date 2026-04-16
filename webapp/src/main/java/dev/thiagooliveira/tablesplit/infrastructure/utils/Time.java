package dev.thiagooliveira.tablesplit.infrastructure.utils;

import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Time {

  private static ZoneId zoneId;

  public Time(@Value("${app.time.zone-id}") String zoneId) {
    dev.thiagooliveira.tablesplit.infrastructure.utils.Time.zoneId = ZoneId.of(zoneId);
    dev.thiagooliveira.tablesplit.domain.common.Time.setZoneId(
        dev.thiagooliveira.tablesplit.infrastructure.utils.Time.zoneId);
  }

  public static ZoneId getZoneId() {
    return zoneId;
  }
}

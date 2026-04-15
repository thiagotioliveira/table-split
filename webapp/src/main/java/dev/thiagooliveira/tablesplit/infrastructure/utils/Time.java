package dev.thiagooliveira.tablesplit.infrastructure.utils;

import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Time {

  private final ZoneId zoneId;

  public Time(@Value("${app.time.zone-id}") String zoneId) {
    this.zoneId = ZoneId.of(zoneId);
  }

  public ZoneId getZoneId() {
    return zoneId;
  }
}

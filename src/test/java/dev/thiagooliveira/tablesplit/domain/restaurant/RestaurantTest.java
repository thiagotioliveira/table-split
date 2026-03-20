package dev.thiagooliveira.tablesplit.domain.restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurantTest {

  private Restaurant restaurant;
  private final ZoneId zoneId = ZoneId.of("UTC");

  @BeforeEach
  void setUp() {
    restaurant = new Restaurant();
    restaurant.setName("Test Restaurant");
  }

  @Test
  void isOpen_shouldReturnFalse_whenDaysIsNull() {
    restaurant.setDays(null);
    ZonedDateTime now = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday
    assertFalse(restaurant.isOpen(now));
  }

  @Test
  void isOpen_shouldReturnFalse_whenDaysIsEmpty() {
    restaurant.setDays(Collections.emptyList());
    ZonedDateTime now = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday
    assertFalse(restaurant.isOpen(now));
  }

  @Test
  void isOpen_shouldReturnFalse_whenClosedOnDay() {
    BusinessHours friday = new BusinessHours("friday", true, Collections.emptyList());
    restaurant.setDays(List.of(friday));

    ZonedDateTime now = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday
    assertFalse(restaurant.isOpen(now));
  }

  @Test
  void isOpen_shouldReturnTrue_whenTimeIsWithinPeriod() {
    Period period = new Period("08:00", "18:00");
    BusinessHours friday = new BusinessHours("friday", false, List.of(period));
    restaurant.setDays(List.of(friday));

    ZonedDateTime now =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday 12:00
    assertTrue(restaurant.isOpen(now));
  }

  @Test
  void isOpen_shouldReturnFalse_whenTimeIsOutsidePeriod() {
    Period period = new Period("08:00", "12:00");
    BusinessHours friday = new BusinessHours("friday", false, List.of(period));
    restaurant.setDays(List.of(friday));

    ZonedDateTime now =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 13, 0), zoneId); // Friday 13:00
    assertFalse(restaurant.isOpen(now));
  }

  @Test
  void isOpen_shouldHandleMidnightCrossing_withinPeriod() {
    // 22:00 to 02:00
    Period period = new Period("22:00", "02:00");
    BusinessHours friday = new BusinessHours("friday", false, List.of(period));
    restaurant.setDays(List.of(friday));

    // Friday 23:00 -> should be open
    ZonedDateTime FridayNight = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 23, 0), zoneId);
    assertTrue(restaurant.isOpen(FridayNight));

    // Friday 01:00 -> should be open (since it's still Friday according to the logic in
    // isOpenAt/isWithinPeriod which only checks currentTime against start/end of the day's periods)
    // Wait, the logic in Restaurant.isOpen uses now.getDayOfWeek().name().toLowerCase()
    // If it's Saturday 01:00, it checks Saturday's periods.
    // If the period is in Friday's BusinessHours ("friday"), and the time is 01:00, isOpen checks
    // "friday" if it's Friday 01:00.
    ZonedDateTime FridayEarly = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 1, 0), zoneId);
    assertTrue(restaurant.isOpen(FridayEarly));
  }

  @Test
  void getNextOpeningOrClosing_shouldReturnClosingTime_whenOpen() {
    Period period = new Period("08:00", "18:00");
    BusinessHours friday = new BusinessHours("friday", false, List.of(period));
    restaurant.setDays(List.of(friday));

    ZonedDateTime now =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday 12:00
    ZonedDateTime expectedClosing = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 18, 0), zoneId);

    assertTrue(restaurant.getNextOpeningOrClosing(now).isPresent());
    assertEquals(
        expectedClosing.toInstant(), restaurant.getNextOpeningOrClosing(now).get().toInstant());
  }

  @Test
  void getNextOpeningOrClosing_shouldReturnOpeningTime_whenClosed_sameDay() {
    Period period = new Period("18:00", "22:00");
    BusinessHours friday = new BusinessHours("friday", false, List.of(period));
    restaurant.setDays(List.of(friday));

    ZonedDateTime now =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday 12:00
    ZonedDateTime expectedOpening = ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 18, 0), zoneId);

    assertTrue(restaurant.getNextOpeningOrClosing(now).isPresent());
    assertEquals(
        expectedOpening.toInstant(), restaurant.getNextOpeningOrClosing(now).get().toInstant());
  }

  @Test
  void getNextOpeningOrClosing_shouldReturnOpeningTime_whenClosed_nextDay() {
    Period satPeriod = new Period("08:00", "12:00");
    BusinessHours friday = new BusinessHours("friday", true, Collections.emptyList());
    BusinessHours saturday = new BusinessHours("saturday", false, List.of(satPeriod));
    restaurant.setDays(List.of(friday, saturday));

    ZonedDateTime now =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday 12:00
    ZonedDateTime expectedOpening =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 21, 8, 0), zoneId); // Saturday 08:00

    assertTrue(restaurant.getNextOpeningOrClosing(now).isPresent());
    assertEquals(
        expectedOpening.toInstant(), restaurant.getNextOpeningOrClosing(now).get().toInstant());
  }

  @Test
  void getNextOpeningOrClosing_shouldReturnEmpty_whenAlwaysClosed() {
    BusinessHours friday = new BusinessHours("friday", true, Collections.emptyList());
    restaurant.setDays(List.of(friday));

    ZonedDateTime now =
        ZonedDateTime.of(LocalDateTime.of(2026, 3, 20, 12, 0), zoneId); // Friday 12:00
    assertTrue(restaurant.getNextOpeningOrClosing(now).isEmpty());
  }
}

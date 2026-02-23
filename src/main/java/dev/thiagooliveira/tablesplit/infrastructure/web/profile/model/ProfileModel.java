package dev.thiagooliveira.tablesplit.infrastructure.web.profile.model;

import static dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.ZONE;

import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.utils.ColorUtils;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProfileModel {
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

  private final String name;
  private final String description;
  private final String website;
  private final String phone;
  private final String averagePrice;
  private final String email;
  private final String address;
  private final List<String> tags;
  private final boolean open;
  private final ZonedDateTime nextOpeningOrClosingHours;
  private final List<DaySchedule> schedule;
  private final String hashPrimaryColor;
  private final String hashAccentColor;
  private final String hashGradientColor;
  private final String hashPrimaryShadowColor;

  public ProfileModel(Restaurant restaurant) {
    this.name = restaurant.getName();
    this.description = restaurant.getDescription();
    this.website = restaurant.getWebsite();
    this.phone = restaurant.getPhone();
    String[] values = restaurant.getAveragePrice().split("-");
    var symbol = CurrencyMapper.symbol(restaurant.getCurrency());
    this.averagePrice = String.format("%s %s - %s %s", symbol, values[0], symbol, values[1]);
    this.email = restaurant.getEmail();
    this.address = restaurant.getAddress();
    this.tags =
        restaurant.getTags() == null
            ? List.of()
            : restaurant.getTags().stream().map(Enum::name).toList();
    var now = ZonedDateTime.now(ZONE);
    this.open = restaurant.isOpen(now);
    this.nextOpeningOrClosingHours = restaurant.getNextOpeningOrClosing(now);

    Map<String, BusinessHours> map =
        restaurant.getDays().stream().collect(Collectors.toMap(BusinessHours::getDay, d -> d));

    this.schedule = new ArrayList<>();

    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {

      String key = dayOfWeek.name().toLowerCase();
      BusinessHours bh = map.get(key);

      if (bh == null || bh.isClosed()) {
        this.schedule.add(new DaySchedule(key, true, null));
        continue;
      }

      String periods =
          bh.getPeriods().stream()
              .map(
                  p -> {
                    LocalTime start = LocalTime.parse(p.getStart());
                    LocalTime end = LocalTime.parse(p.getEnd());
                    return start.format(formatter) + " - " + end.format(formatter);
                  })
              .collect(Collectors.joining(", "));

      schedule.add(new DaySchedule(key, false, periods));
    }

    this.hashPrimaryColor = restaurant.getHashPrimaryColor();
    this.hashAccentColor = restaurant.getHashAccentColor();
    this.hashGradientColor = ColorUtils.lighten(this.hashPrimaryColor, 0.08);
    this.hashPrimaryShadowColor =
        ColorUtils.darkenAndConvertToRgba(this.hashPrimaryColor, 0.15, 0.3);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getWebsite() {
    return website;
  }

  public String getPhone() {
    return phone;
  }

  public String getAveragePrice() {
    return averagePrice;
  }

  public String getEmail() {
    return email;
  }

  public String getAddress() {
    return address;
  }

  public List<String> getTags() {
    return tags;
  }

  public boolean isOpen() {
    return open;
  }

  public ZonedDateTime getNextOpeningOrClosingHours() {
    return nextOpeningOrClosingHours;
  }

  public List<DaySchedule> getSchedule() {
    return schedule;
  }

  public String getHashPrimaryColor() {
    return hashPrimaryColor;
  }

  public String getHashAccentColor() {
    return hashAccentColor;
  }

  public String getHashGradientColor() {
    return hashGradientColor;
  }

  public String getHashPrimaryShadowColor() {
    return hashPrimaryShadowColor;
  }
}

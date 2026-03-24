package dev.thiagooliveira.tablesplit.infrastructure.web.customer.profile.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.utils.ColorUtils;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery.model.RestaurantImageModel;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;

public class ProfileModel {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

  private final String name;
  private final String description;
  private final String website;
  private final String phone;
  private final String averagePrice;
  private final String email;
  private final String address;
  private final String cuisineType;
  private final List<String> tags;
  private final boolean open;
  private final Optional<ZonedDateTime> nextOpeningOrClosingHours;
  private final List<DaySchedule> schedule;
  private final String hashPrimaryColor;
  private final String hashAccentColor;
  private final String hashGradientColor;
  private final String hashPrimaryShadowColor;
  private final List<RestaurantImageModel> images;

  private final String menuLink;

  private final MessageSource messageSource;

  public ProfileModel(Restaurant restaurant, ZoneId zoneId, MessageSource messageSource) {
    this.menuLink = String.format("/@%s/menu", restaurant.getSlug());
    this.name = restaurant.getName();
    this.cuisineType =
        restaurant.getCuisineType() != null
            ? CuisineType.valueOf(restaurant.getCuisineType().name()).getLabel()
            : null;
    this.description = restaurant.getDescription();
    this.website = restaurant.getWebsite();
    this.phone = restaurant.getPhone();
    this.messageSource = messageSource;
    String[] values = restaurant.getAveragePrice().split("-");
    var symbol = restaurant.getCurrency().getSymbol();
    this.averagePrice = String.format("%s %s - %s %s", symbol, values[0], symbol, values[1]);
    this.email = restaurant.getEmail();
    this.address = restaurant.getAddress();
    this.tags =
        restaurant.getTags() == null
            ? List.of()
            : restaurant.getTags().stream().map(Enum::name).toList();
    var now = ZonedDateTime.now(zoneId);
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
    this.images =
        restaurant.getImages() == null
            ? List.of()
            : restaurant.getImages().stream().map(RestaurantImageModel::new).toList();
  }

  public String getCuisineType() {
    return cuisineType;
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

  public String getNextOpeningOrClosingHoursDisplay(Locale locale) {
    if (nextOpeningOrClosingHours.isEmpty()) {
      return messageSource.getMessage("page.profile.status.empty", null, locale);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    if (open) {
      return messageSource.getMessage(
          "page.profile.status.hours.close",
          new Object[] {nextOpeningOrClosingHours.get().format(formatter)},
          locale);
    } else {
      String day = nextOpeningOrClosingHours.get().getDayOfWeek().name().toLowerCase();
      return messageSource.getMessage(
          "page.profile.status.hours.open",
          new Object[] {day, nextOpeningOrClosingHours.get().format(formatter)},
          locale);
    }
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

  public String getMenuLink() {
    return menuLink;
  }

  public List<RestaurantImageModel> getImages() {
    return images;
  }

  public List<RestaurantImageModel> getCoverImages() {
    return images.stream().filter(RestaurantImageModel::isCover).toList();
  }

  public List<RestaurantImageModel> getGalleryImages() {
    return images.stream().filter(img -> !img.isCover()).toList();
  }

  public String getGoogleMapsEmbedUrl() {
    if (address == null || address.isBlank()) {
      return null;
    }

    String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
    return "https://www.google.com/maps?q=" + encodedAddress + "&output=embed";
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery.model.RestaurantImageModel;
import java.util.List;

public class RestaurantModel {
  private final String id;
  private final String name;
  private final String description;
  private final String address;
  private final String slug;
  private final Currency currency;
  private final String cuisineType;
  private final String averagePrice;
  private final List<RestaurantImageModel> images;
  private final List<Language> customerLanguages;
  private final Language defaultLanguage;
  private final double serviceFee;
  private final boolean open;
  private final java.util.Optional<java.time.ZonedDateTime> nextOpeningOrClosingHours;
  private final org.springframework.context.MessageSource messageSource;

  public RestaurantModel(
      Restaurant restaurant,
      java.time.ZoneId zoneId,
      org.springframework.context.MessageSource messageSource) {
    this.id = restaurant.getId().toString();
    this.name = restaurant.getName();
    this.description = restaurant.getDescription();
    this.address = restaurant.getAddress();
    this.slug = restaurant.getSlug();
    this.currency = restaurant.getCurrency();
    this.cuisineType =
        restaurant.getCuisineType() != null
            ? dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType
                .valueOf(restaurant.getCuisineType().name())
                .getLabel()
            : null;
    String label =
        restaurant.getAveragePrice() != null ? restaurant.getAveragePrice().getLabel() : "0-0";
    String[] values = label.split("-");
    var symbol = restaurant.getCurrency().getSymbol();
    this.averagePrice = String.format("%s %s - %s %s", symbol, values[0], symbol, values[1]);
    this.images =
        restaurant.getImages() == null
            ? List.of()
            : restaurant.getImages().stream().map(RestaurantImageModel::new).toList();
    this.customerLanguages =
        restaurant.getCustomerLanguages().stream().map(Language::fromDomain).toList();
    this.defaultLanguage = Language.fromDomain(restaurant.getDefaultLanguage());
    this.serviceFee = restaurant.getServiceFee() / 100d;

    var now = java.time.ZonedDateTime.now(zoneId);
    this.open = restaurant.isOpen(now);
    this.nextOpeningOrClosingHours = restaurant.getNextOpeningOrClosing(now);
    this.messageSource = messageSource;
  }

  public String getSlug() {
    return slug;
  }

  public String getId() {
    return id;
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

  public String getAddress() {
    return address;
  }

  public String getAveragePrice() {
    return averagePrice;
  }

  public List<RestaurantImageModel> getImages() {
    return images;
  }

  public List<RestaurantImageModel> getCoverImages() {
    return images.stream().filter(RestaurantImageModel::isCover).toList();
  }

  public double getServiceFee() {
    return serviceFee;
  }

  public Currency getCurrency() {
    return currency;
  }

  public List<dev.thiagooliveira.tablesplit.infrastructure.web.Language> getCustomerLanguages() {
    return customerLanguages;
  }

  public dev.thiagooliveira.tablesplit.infrastructure.web.Language getDefaultLanguage() {
    return defaultLanguage;
  }

  public boolean isOpen() {
    return open;
  }

  public String getNextOpeningOrClosingHoursDisplay(java.util.Locale locale) {
    if (nextOpeningOrClosingHours.isEmpty()) {
      return messageSource.getMessage("page.profile.status.empty", null, locale);
    }

    java.time.format.DateTimeFormatter formatter =
        java.time.format.DateTimeFormatter.ofPattern("HH:mm");
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
}

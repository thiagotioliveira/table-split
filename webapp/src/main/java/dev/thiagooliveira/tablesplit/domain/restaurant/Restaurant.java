package dev.thiagooliveira.tablesplit.domain.restaurant;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Restaurant extends AggregateRoot {
  private UUID id;
  private UUID accountId;
  private String name;
  private String slug;
  private String description;
  private String website;
  private String phone;
  private String email;
  private String address;
  private CuisineType cuisineType;
  private List<Tag> tags;
  private List<Language> customerLanguages;
  private Currency currency;
  private int serviceFee;
  private AveragePrice averagePrice;
  private List<BusinessHours> days;
  private String hashPrimaryColor;
  private String hashAccentColor;
  private ThemeName themeName = ThemeName.DEFAULT;
  private String hashBackgroundColor;
  private String hashCardColor;
  private String hashTextColor;
  private List<RestaurantImage> images;
  private Language defaultLanguage;

  public static Restaurant create(
      UUID id, UUID accountId, String name, String slug, int numberOfTables) {
    Restaurant restaurant = new Restaurant();
    restaurant.setId(id);
    restaurant.setAccountId(accountId);
    restaurant.setName(name);
    restaurant.setSlug(slug);
    restaurant.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent(
            restaurant, numberOfTables));
    return restaurant;
  }

  public void update(
      String name,
      String slug,
      String description,
      String website,
      String phone,
      String email,
      String address,
      CuisineType cuisineType,
      List<Language> customerLanguages,
      List<Tag> tags,
      Currency currency,
      int serviceFee,
      AveragePrice averagePrice,
      List<BusinessHours> days,
      String hashPrimaryColor,
      String hashAccentColor,
      ThemeName themeName,
      String hashBackgroundColor,
      String hashCardColor,
      String hashTextColor) {

    var oldLanguages =
        this.customerLanguages != null ? this.customerLanguages : List.<Language>of();
    var newLanguages = customerLanguages != null ? customerLanguages : List.<Language>of();

    List<Language> added = newLanguages.stream().filter(l -> !oldLanguages.contains(l)).toList();
    List<Language> removed = oldLanguages.stream().filter(l -> !newLanguages.contains(l)).toList();

    this.name = name;
    this.slug = slug;
    this.description = description;
    this.website = website;
    this.phone = phone;
    this.email = email;
    this.address = address;
    this.cuisineType = cuisineType;
    this.customerLanguages = customerLanguages;
    this.tags = tags;
    this.currency = currency;
    this.serviceFee = serviceFee;
    this.averagePrice = averagePrice;
    this.days = days;
    this.hashPrimaryColor = hashPrimaryColor;
    this.hashAccentColor = hashAccentColor;
    this.themeName = themeName != null ? themeName : ThemeName.DEFAULT;
    this.hashBackgroundColor = hashBackgroundColor;
    this.hashCardColor = hashCardColor;
    this.hashTextColor = hashTextColor;

    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.RestaurantUpdatedEvent(
            this, added, removed));
  }

  public Language getDefaultLanguage() {
    return defaultLanguage;
  }

  public void setDefaultLanguage(Language defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
  }

  public List<RestaurantImage> getImages() {
    return images;
  }

  public void setImages(List<RestaurantImage> images) {
    this.images = images;
  }

  public Optional<ZonedDateTime> getNextOpeningOrClosing(ZonedDateTime now) {

    boolean currentlyOpen = isOpen(now);

    for (int i = 0; i < 7; i++) {

      ZonedDateTime dateToCheck = now.plusDays(i);
      String dayString = dateToCheck.getDayOfWeek().name().toLowerCase();

      Optional<BusinessHours> optional =
          days.stream()
              .filter(d -> dayString.equals(d.getDay()))
              .filter(d -> !d.isClosed())
              .findFirst();

      if (optional.isEmpty()) continue;

      BusinessHours bh = optional.get();

      for (Period period : bh.getPeriods()) {

        LocalTime start = LocalTime.parse(period.getStart(), Period.TIME_FORMATTER);
        LocalTime end = LocalTime.parse(period.getEnd(), Period.TIME_FORMATTER);

        ZonedDateTime startDateTime = dateToCheck.with(start);

        ZonedDateTime endDateTime =
            end.isAfter(start) ? dateToCheck.with(end) : dateToCheck.plusDays(1).with(end);

        if (currentlyOpen) {
          if (now.isBefore(endDateTime)) {
            return Optional.of(endDateTime);
          }
        } else {
          if (now.isBefore(startDateTime)) {
            return Optional.of(startDateTime);
          }
        }
      }
    }

    return Optional.empty();
  }

  public boolean isOpen(ZonedDateTime now) {

    if (days == null || days.isEmpty()) {
      return false;
    }

    String currentDay = now.getDayOfWeek().name().toLowerCase();
    LocalTime currentTime = now.toLocalTime();

    return days.stream()
        .filter(d -> currentDay.equals(d.getDay()))
        .filter(d -> !d.isClosed())
        .findFirst()
        .map(d -> isOpenAt(d, currentTime))
        .orElse(false);
  }

  private boolean isOpenAt(BusinessHours businessHours, LocalTime currentTime) {

    if (businessHours.getPeriods() == null) {
      return false;
    }

    for (Period period : businessHours.getPeriods()) {

      LocalTime start = LocalTime.parse(period.getStart(), Period.TIME_FORMATTER);
      LocalTime end = LocalTime.parse(period.getEnd(), Period.TIME_FORMATTER);

      if (isWithinPeriod(currentTime, start, end)) {
        return true;
      }
    }

    return false;
  }

  private boolean isWithinPeriod(LocalTime now, LocalTime start, LocalTime end) {

    // Caso normal (08:00 → 18:00)
    if (end.isAfter(start)) {
      return !now.isBefore(start) && now.isBefore(end);
    }

    // Caso atravessa meia-noite (18:00 → 02:00)
    return !now.isBefore(start) || now.isBefore(end);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public CuisineType getCuisineType() {
    return cuisineType;
  }

  public void setCuisineType(CuisineType cuisineType) {
    this.cuisineType = cuisineType;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public List<Language> getCustomerLanguages() {
    return customerLanguages;
  }

  public void setCustomerLanguages(List<Language> customerLanguages) {
    this.customerLanguages = customerLanguages;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public int getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(int serviceFee) {
    this.serviceFee = serviceFee;
  }

  public AveragePrice getAveragePrice() {
    return averagePrice;
  }

  public void setAveragePrice(AveragePrice averagePrice) {
    this.averagePrice = averagePrice;
  }

  public List<BusinessHours> getDays() {
    return days;
  }

  public void setDays(List<BusinessHours> days) {
    this.days = days;
  }

  public String getHashPrimaryColor() {
    return hashPrimaryColor;
  }

  public void setHashPrimaryColor(String hashPrimaryColor) {
    this.hashPrimaryColor = hashPrimaryColor;
  }

  public String getHashAccentColor() {
    return hashAccentColor;
  }

  public void setHashAccentColor(String hashAccentColor) {
    this.hashAccentColor = hashAccentColor;
  }

  public ThemeName getThemeName() {
    return themeName;
  }

  public void setThemeName(ThemeName themeName) {
    this.themeName = themeName;
  }

  public String getHashBackgroundColor() {
    return hashBackgroundColor;
  }

  public void setHashBackgroundColor(String hashBackgroundColor) {
    this.hashBackgroundColor = hashBackgroundColor;
  }

  public String getHashCardColor() {
    return hashCardColor;
  }

  public void setHashCardColor(String hashCardColor) {
    this.hashCardColor = hashCardColor;
  }

  public String getHashTextColor() {
    return hashTextColor;
  }

  public void setHashTextColor(String hashTextColor) {
    this.hashTextColor = hashTextColor;
  }
}

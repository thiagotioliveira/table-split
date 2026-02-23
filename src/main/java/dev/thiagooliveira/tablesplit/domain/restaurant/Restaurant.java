package dev.thiagooliveira.tablesplit.domain.restaurant;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Restaurant {
  private UUID id;
  private String name;
  private String slug;
  private String description;
  private String website;
  private String phone;
  private String email;
  private String address;
  private List<CuisineType> cuisineType;
  private List<Tag> tags;
  private String defaultLanguage;
  private List<Language> customerLanguages;
  private String currency;
  private int serviceFee;
  private String averagePrice;
  private List<BusinessHours> days;
  private String hashPrimaryColor;
  private String hashAccentColor;

  public ZonedDateTime getNextOpeningOrClosing(ZonedDateTime now) {

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
            return endDateTime;
          }
        } else {
          if (now.isBefore(startDateTime)) {
            return startDateTime;
          }
        }
      }
    }

    return null; // nunca abre
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

  public List<CuisineType> getCuisineType() {
    return cuisineType;
  }

  public void setCuisineType(List<CuisineType> cuisineType) {
    this.cuisineType = cuisineType;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public String getDefaultLanguage() {
    return defaultLanguage;
  }

  public void setDefaultLanguage(String defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
  }

  public List<Language> getCustomerLanguages() {
    return customerLanguages;
  }

  public void setCustomerLanguages(List<Language> customerLanguages) {
    this.customerLanguages = customerLanguages;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(int serviceFee) {
    this.serviceFee = serviceFee;
  }

  public String getAveragePrice() {
    return averagePrice;
  }

  public void setAveragePrice(String averagePrice) {
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
}

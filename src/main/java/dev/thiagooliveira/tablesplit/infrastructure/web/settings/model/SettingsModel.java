package dev.thiagooliveira.tablesplit.infrastructure.web.settings.model;

import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import jakarta.validation.constraints.*;
import java.util.List;

public class SettingsModel {
  @NotBlank private String name;

  @NotBlank
  @Size(min = 3, max = 50)
  @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "{validation.slug.invalid}")
  private String slug;

  @Size(max = 254)
  private String description;

  @Size(max = 254)
  private String website;

  @Size(max = 20)
  private String phone;

  @NotBlank
  @Size(max = 254)
  @Email
  private String email;

  @Size(max = 254)
  private String address;

  private List<String> cuisineType;

  private List<String> tags;

  @Size(max = 5)
  @NotBlank
  private String defaultLanguage;

  private List<LanguageModel> customerLanguages;

  @Size(min = 3, max = 3)
  @NotBlank
  private String currency;

  @Min(0)
  @Max(100)
  @NotNull
  private int serviceFee;

  @NotBlank
  @Size(max = 10)
  private String averagePrice;

  private List<BusinessHoursModel> days;

  @NotBlank
  @Size(min = 7, max = 7)
  private String hashPrimaryColor;

  @NotBlank
  @Size(min = 7, max = 7)
  private String hashAccentColor;

  public SettingsModel() {}

  public SettingsModel(Restaurant restaurant) {
    this.name = restaurant.getName();
    this.slug = restaurant.getSlug();
    this.description = restaurant.getDescription();
    this.website = restaurant.getWebsite();
    this.phone = restaurant.getPhone();
    this.email = restaurant.getEmail();
    this.address = restaurant.getAddress();
    this.cuisineType =
        restaurant.getCuisineType() == null
            ? List.of()
            : restaurant.getCuisineType().stream().map(Enum::name).toList();
    this.tags =
        restaurant.getTags() == null
            ? List.of()
            : restaurant.getTags().stream().map(Enum::name).toList();
    this.defaultLanguage = restaurant.getDefaultLanguage();
    this.customerLanguages =
        restaurant.getCustomerLanguages() == null
            ? List.of()
            : restaurant.getCustomerLanguages().stream().map(LanguageModel::new).toList();
    this.currency = restaurant.getCurrency();
    this.serviceFee = restaurant.getServiceFee();
    this.averagePrice = restaurant.getAveragePrice();
    this.days =
        restaurant.getDays() == null
            ? List.of()
            : restaurant.getDays().stream().map(BusinessHoursModel::new).toList();
    this.hashPrimaryColor = restaurant.getHashPrimaryColor();
    this.hashAccentColor = restaurant.getHashAccentColor();
  }

  public UpdateRestaurantCommand toCommand() {
    List<Tag> domainTags =
        this.tags == null ? List.of() : this.tags.stream().map(Tag::valueOf).toList();
    List<BusinessHours> domainDays =
        this.days == null
            ? List.of()
            : this.days.stream().map(BusinessHoursModel::toCommand).toList();
    List<Language> domainLanguage =
        this.customerLanguages == null
            ? List.of()
            : this.customerLanguages.stream().map(LanguageModel::toCommand).toList();
    List<CuisineType> domainCuisineType =
        this.cuisineType == null
            ? List.of()
            : this.cuisineType.stream().map(CuisineType::valueOf).toList();
    return new UpdateRestaurantCommand(
        this.name,
        this.slug,
        this.description,
        this.website,
        this.phone,
        this.email,
        this.address,
        domainCuisineType,
        domainTags,
        this.defaultLanguage,
        domainLanguage,
        this.currency,
        this.serviceFee,
        this.averagePrice,
        domainDays,
        this.hashPrimaryColor,
        this.hashAccentColor);
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

  public List<String> getCuisineType() {
    return cuisineType;
  }

  public void setCuisineType(List<String> cuisineType) {
    this.cuisineType = cuisineType;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getDefaultLanguage() {
    return defaultLanguage;
  }

  public void setDefaultLanguage(String defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
  }

  public List<LanguageModel> getCustomerLanguages() {
    return customerLanguages;
  }

  public void setCustomerLanguages(List<LanguageModel> customerLanguages) {
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

  public List<BusinessHoursModel> getDays() {
    return days;
  }

  public void setDays(List<BusinessHoursModel> days) {
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

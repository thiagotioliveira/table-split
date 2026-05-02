package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID accountId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String slug;

  private String description;

  private String website;

  private String phone;

  @Column(nullable = false)
  private String email;

  private String address;

  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  private CuisineType cuisineType;

  @Convert(converter = TagJsonListConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<Tag> tags = new ArrayList<>();

  @Convert(converter = LanguageJsonListConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<Language> customerLanguages = new ArrayList<>();

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Column(nullable = false)
  private int serviceFee;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private AveragePrice averagePrice;

  @Convert(converter = BusinessHoursJsonListConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<BusinessHours> days = new ArrayList<>();

  @Column(nullable = false)
  private String hashPrimaryColor;

  @Column(nullable = false)
  private String hashAccentColor;

  @Enumerated(EnumType.STRING)
  @Column(name = "default_language", nullable = false)
  private Language defaultLanguage;

  @Enumerated(EnumType.STRING)
  @Column(name = "theme_name", nullable = false)
  private dev.thiagooliveira.tablesplit.domain.restaurant.ThemeName themeName =
      dev.thiagooliveira.tablesplit.domain.restaurant.ThemeName.DEFAULT;

  @Column(name = "hash_background_color")
  private String hashBackgroundColor;

  @Column(name = "hash_card_color")
  private String hashCardColor;

  @Column(name = "hash_text_color")
  private String hashTextColor;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RestaurantEntity that = (RestaurantEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
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

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public Language getDefaultLanguage() {
    return defaultLanguage;
  }

  public void setDefaultLanguage(Language defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
  }

  public dev.thiagooliveira.tablesplit.domain.restaurant.ThemeName getThemeName() {
    return themeName;
  }

  public void setThemeName(dev.thiagooliveira.tablesplit.domain.restaurant.ThemeName themeName) {
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

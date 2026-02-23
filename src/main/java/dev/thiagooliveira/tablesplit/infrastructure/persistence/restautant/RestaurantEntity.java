package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

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
  private String name;

  @Column(nullable = false)
  private String slug;

  private String description;

  private String phone;

  @Column(nullable = false)
  private String email;

  private String address;

  @Convert(converter = CuisineTypeJsonListConverter.class)
  @Column(nullable = false)
  private List<CuisineType> cuisineType = new ArrayList<>();

  @Convert(converter = TagJsonListConverter.class)
  private List<Tag> tags = new ArrayList<>();

  @Column(nullable = false)
  private String defaultLanguage;

  @Convert(converter = LanguageJsonListConverter.class)
  private List<Language> customerLanguages = new ArrayList<>();

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private int serviceFee;

  @Column(nullable = false)
  private String averagePrice;

  @Convert(converter = BusinessHoursJsonListConverter.class)
  private List<BusinessHours> days = new ArrayList<>();

  @Column(nullable = false)
  private String hashPrimaryColor;

  @Column(nullable = false)
  private String hashAccentColor;

  public Restaurant toDomain() {
    var domain = new Restaurant();
    domain.setId(this.id);
    domain.setName(this.name);
    domain.setSlug(this.slug);
    domain.setDescription(this.description);
    domain.setPhone(this.phone);
    domain.setEmail(this.email);
    domain.setAddress(this.address);
    domain.setCuisineType(this.cuisineType);
    domain.setTags(this.tags);
    domain.setDefaultLanguage(this.defaultLanguage);
    domain.setCustomerLanguages(this.customerLanguages);
    domain.setCurrency(this.currency);
    domain.setServiceFee(this.serviceFee);
    domain.setAveragePrice(this.averagePrice);
    domain.setDays(this.days);
    domain.setHashPrimaryColor(this.hashPrimaryColor);
    domain.setHashAccentColor(this.hashAccentColor);
    return domain;
  }

  public static RestaurantEntity fromDomain(Restaurant domain) {
    var entity = new RestaurantEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setSlug(domain.getSlug());
    entity.setDescription(domain.getDescription());
    entity.setPhone(domain.getPhone());
    entity.setEmail(domain.getEmail());
    entity.setAddress(domain.getAddress());
    entity.setCuisineType(domain.getCuisineType());
    entity.setTags(domain.getTags());
    entity.setDefaultLanguage(domain.getDefaultLanguage());
    entity.setCustomerLanguages(domain.getCustomerLanguages());
    entity.setCurrency(domain.getCurrency());
    entity.setServiceFee(domain.getServiceFee());
    entity.setAveragePrice(domain.getAveragePrice());
    entity.setDays(domain.getDays());
    entity.setHashPrimaryColor(domain.getHashPrimaryColor());
    entity.setHashAccentColor(domain.getHashAccentColor());
    return entity;
  }

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

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }
}

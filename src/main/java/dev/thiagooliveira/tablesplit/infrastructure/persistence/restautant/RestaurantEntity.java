package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  private String description;

  private String phone;

  @Column(nullable = false)
  private String email;

  private String address;

  @Column(nullable = false)
  private String defaultLanguage;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private int serviceFee;

  @Column(nullable = false)
  private String averagePrice;

  @Column(nullable = false)
  private String hashPrimaryColor;

  @Column(nullable = false)
  private String hashAccentColor;

  public Restaurant toDomain() {
    var domain = new Restaurant();
    domain.setId(this.id);
    domain.setName(this.name);
    domain.setDescription(this.description);
    domain.setPhone(this.phone);
    domain.setEmail(this.email);
    domain.setAddress(this.address);
    domain.setDefaultLanguage(this.defaultLanguage);
    domain.setCurrency(this.currency);
    domain.setServiceFee(this.serviceFee);
    domain.setAveragePrice(this.averagePrice);
    domain.setHashPrimaryColor(this.hashPrimaryColor);
    domain.setHashAccentColor(this.hashAccentColor);
    return domain;
  }

  public static RestaurantEntity fromDomain(Restaurant domain) {
    var entity = new RestaurantEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setPhone(domain.getPhone());
    entity.setEmail(domain.getEmail());
    entity.setAddress(domain.getAddress());
    entity.setDefaultLanguage(domain.getDefaultLanguage());
    entity.setCurrency(domain.getCurrency());
    entity.setServiceFee(domain.getServiceFee());
    entity.setAveragePrice(domain.getAveragePrice());
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

  public String getDefaultLanguage() {
    return defaultLanguage;
  }

  public void setDefaultLanguage(String defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
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

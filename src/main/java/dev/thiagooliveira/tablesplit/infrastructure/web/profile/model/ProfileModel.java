package dev.thiagooliveira.tablesplit.infrastructure.web.profile.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;

public class ProfileModel {
  private final String name;
  private final String description;
  private final String website;
  private final String phone;
  private final String averagePrice;
  private final String email;
  private final String address;
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

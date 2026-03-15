package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import java.util.UUID;

public class AccountCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final AccountCreatedEventDetails details;

  public AccountCreatedEvent(UUID accountId, AccountCreatedEventDetails details) {
    this.accountId = accountId;
    this.details = details;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public AccountCreatedEventDetails getDetails() {
    return this.details;
  }

  public static class AccountCreatedEventDetails {
    private final RestaurantDetails restaurantDetails;

    public AccountCreatedEventDetails(
        String name,
        String slug,
        String description,
        String phone,
        String email,
        String website,
        String address,
        Currency currency,
        int serviceFee) {
      this.restaurantDetails =
          new RestaurantDetails(
              name, slug, description, phone, email, website, address, currency, serviceFee);
    }

    public RestaurantDetails getRestaurantDetails() {
      return restaurantDetails;
    }
  }

  public static class RestaurantDetails {
    private final String name;
    private final String slug;
    private final String description;
    private final String phone;
    private final String email;
    private final String website;
    private final String address;
    private final Currency currency;
    private final int serviceFee;

    public RestaurantDetails(
        String name,
        String slug,
        String description,
        String phone,
        String email,
        String website,
        String address,
        Currency currency,
        int serviceFee) {
      this.name = name;
      this.slug = slug;
      this.description = description;
      this.phone = phone;
      this.email = email;
      this.website = website;
      this.address = address;
      this.currency = currency;
      this.serviceFee = serviceFee;
    }

    public String getName() {
      return name;
    }

    public String getSlug() {
      return slug;
    }

    public String getDescription() {
      return description;
    }

    public String getPhone() {
      return phone;
    }

    public String getEmail() {
      return email;
    }

    public String getWebsite() {
      return website;
    }

    public String getAddress() {
      return address;
    }

    public Currency getCurrency() {
      return currency;
    }

    public int getServiceFee() {
      return serviceFee;
    }
  }
}

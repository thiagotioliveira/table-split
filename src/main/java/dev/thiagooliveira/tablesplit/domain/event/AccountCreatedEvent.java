package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.util.UUID;

public class AccountCreatedEvent implements DomainEvent {
  private final UUID accountId;
  private final RestaurantData restaurantData;

  public AccountCreatedEvent(UUID accountId, RestaurantData restaurantData) {
    this.accountId = accountId;
    this.restaurantData = restaurantData;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public RestaurantData getRestaurantData() {
    return restaurantData;
  }

  public static class RestaurantData {
    private final String name;
    private final String slug;
    private final String description;
    private final String phone;
    private final String email;
    private final String website;
    private final String address;
    private final Language defaultLanguage;
    private final String currency;
    private final int serviceFee;

    public RestaurantData(CreateRestaurantCommand command) {
      this.name = command.name();
      this.slug = command.slug();
      this.description = command.description();
      this.phone = command.phone();
      this.email = command.email();
      this.website = command.website();
      this.address = command.address();
      this.defaultLanguage = command.defaultLanguage();
      this.currency = command.currency();
      this.serviceFee = command.serviceFee();
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

    public Language getDefaultLanguage() {
      return defaultLanguage;
    }

    public String getCurrency() {
      return currency;
    }

    public int getServiceFee() {
      return serviceFee;
    }
  }
}

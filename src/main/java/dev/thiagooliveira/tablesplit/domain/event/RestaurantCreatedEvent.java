package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.UUID;

public class RestaurantCreatedEvent
    implements DomainEvent<RestaurantCreatedEvent.RestaurantCreatedEventDetails> {
  private final UUID accountId;
  private final UUID restaurantId;
  private final RestaurantCreatedEventDetails details;

  public RestaurantCreatedEvent(UUID accountId, Restaurant restaurant) {
    this.accountId = accountId;
    this.restaurantId = restaurant.getId();
    this.details = new RestaurantCreatedEventDetails(restaurant);
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  @Override
  public RestaurantCreatedEventDetails getDetails() {
    return this.details;
  }

  public static class RestaurantCreatedEventDetails {
    private final String name;
    private final String slug;
    private final String description;
    private final String phone;
    private final String email;
    private final String website;
    private final String address;
    private final String currency;
    private final int serviceFee;

    public RestaurantCreatedEventDetails(Restaurant restaurant) {
      this.name = restaurant.getName();
      this.slug = restaurant.getSlug();
      this.description = restaurant.getDescription();
      this.phone = restaurant.getPhone();
      this.email = restaurant.getEmail();
      this.website = restaurant.getWebsite();
      this.address = restaurant.getAddress();
      this.currency = restaurant.getCurrency();
      this.serviceFee = restaurant.getServiceFee();
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

    public String getCurrency() {
      return currency;
    }

    public int getServiceFee() {
      return serviceFee;
    }
  }
}

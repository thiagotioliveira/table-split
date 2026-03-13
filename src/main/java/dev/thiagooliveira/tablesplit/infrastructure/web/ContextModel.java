package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

public class ContextModel {

  private final UserContextModel user;
  private final RestaurantContextModel restaurant;

  public ContextModel(Authentication auth) {
    var context = (UserContext) auth.getPrincipal();
    this.user =
        new UserContextModel(
            context.getId(), context.getFirstName(), context.getLastName(), context.getEmail());
    this.restaurant =
        new RestaurantContextModel(
            context.getRestaurant().getId(),
            context.getRestaurant().getName(),
            context.getRestaurant().getCurrency(),
            context.getRestaurant().getCustomerLanguages());
  }

  public UserContextModel getUser() {
    return user;
  }

  public RestaurantContextModel getRestaurant() {
    return restaurant;
  }

  public static class UserContextModel {
    private final UUID id;
    private final String name;
    private final String firstName;
    private final String lastName;
    private final String email;

    public UserContextModel(UUID id, String firstName, String lastName, String email) {
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
      this.name = String.format("%s %s", this.firstName, this.lastName);
      this.email = email;
    }

    public UUID getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public String getEmail() {
      return email;
    }
  }

  public static class RestaurantContextModel {
    private final UUID id;
    private final String name;
    private final Currency currency;
    private final List<Language> customerLanguages;

    public RestaurantContextModel(
        UUID id, String name, Currency currency, List<Language> customerLanguages) {
      this.id = id;
      this.name = name;
      this.currency = currency;
      this.customerLanguages = customerLanguages;
    }

    public UUID getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public Currency getCurrency() {
      return currency;
    }

    public List<Language> getCustomerLanguages() {
      return customerLanguages;
    }
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

public class ContextModel {

  private final UserContextModel user;
  private final RestaurantContextModel restaurant;
  private final List<Module> sidebarModules;
  private final List<Module> footerModules;
  private final long pendingOrdersCount;
  private final long waiterCallCount;

  public ContextModel(Authentication auth, long pendingOrdersCount, long waiterCallCount) {
    var account = (AccountContext) auth.getPrincipal();
    var user = account.getUser();
    var restaurant = account.getRestaurant();
    this.user =
        new UserContextModel(
            user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    this.restaurant =
        new RestaurantContextModel(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getCurrency(),
            restaurant.getCustomerLanguages().stream()
                .map(
                    l ->
                        dev.thiagooliveira.tablesplit.infrastructure.web.Language.valueOf(l.name()))
                .toList());
    this.sidebarModules = account.getSidebarModules();
    this.footerModules = account.getFooterModules();
    this.pendingOrdersCount = pendingOrdersCount;
    this.waiterCallCount = waiterCallCount;
  }

  public long getPendingOrdersCount() {
    return pendingOrdersCount;
  }

  public long getWaiterCallCount() {
    return waiterCallCount;
  }

  public UserContextModel getUser() {
    return user;
  }

  public RestaurantContextModel getRestaurant() {
    return restaurant;
  }

  public List<Module> getSidebarModules() {
    return sidebarModules;
  }

  public List<Module> getFooterModules() {
    return footerModules;
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

package dev.thiagooliveira.tablesplit.infrastructure.web.security;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.ThemeContext;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

public class ManagerContextModel {

  private final UserContextModel user;
  private final RestaurantContextModel restaurant;
  private final List<dev.thiagooliveira.tablesplit.infrastructure.web.Module> sidebarModules;
  private final List<dev.thiagooliveira.tablesplit.infrastructure.web.Module> footerModules;
  private final dev.thiagooliveira.tablesplit.domain.account.Plan plan;

  public ManagerContextModel(Authentication auth) {
    var account = (AccountContext) auth.getPrincipal();
    var user = account.getUser();
    var restaurant = account.getRestaurant();
    this.plan = account.getPlan();
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
                .toList(),
            restaurant.getTheme());
    this.sidebarModules = account.getSidebarModules();
    this.footerModules = account.getFooterModules();
  }

  public UserContextModel getUser() {
    return user;
  }

  public RestaurantContextModel getRestaurant() {
    return restaurant;
  }

  public List<dev.thiagooliveira.tablesplit.infrastructure.web.Module> getSidebarModules() {
    return sidebarModules;
  }

  public List<Module> getFooterModules() {
    return footerModules;
  }

  public dev.thiagooliveira.tablesplit.domain.account.Plan getPlan() {
    return plan;
  }

  public boolean isProfessionalOrHigher() {
    return plan == dev.thiagooliveira.tablesplit.domain.account.Plan.PROFESSIONAL
        || plan == dev.thiagooliveira.tablesplit.domain.account.Plan.ENTERPRISE;
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

    public String getAvatar() {
      String avatar = "";
      if (firstName != null && !firstName.isEmpty())
        avatar += firstName.substring(0, 1).toUpperCase();
      if (lastName != null && !lastName.isEmpty()) avatar += lastName.substring(0, 1).toUpperCase();
      return avatar.isEmpty() ? "??" : avatar;
    }
  }

  public static class RestaurantContextModel {
    private final UUID id;
    private final String name;
    private final Currency currency;
    private final List<Language> customerLanguages;
    private final ThemeContext theme;

    public RestaurantContextModel(
        UUID id,
        String name,
        Currency currency,
        List<Language> customerLanguages,
        ThemeContext theme) {
      this.id = id;
      this.name = name;
      this.currency = currency;
      this.customerLanguages = customerLanguages;
      this.theme = theme;
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

    public ThemeContext getTheme() {
      return theme;
    }
  }
}

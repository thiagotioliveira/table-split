package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountContext implements UserDetails {
  private final UUID id;
  private final boolean active;
  private final Plan plan;
  private final UserContext user;
  private final RestaurantContext restaurant;
  private final List<Module> sidebarModules;
  private final List<Module> footerModules;

  public AccountContext(Account account, User user, Restaurant restaurant) {
    this.id = account.getId();
    this.active = account.isActive();
    this.plan = account.getPlan();
    this.user = new UserContext(user);
    this.restaurant = new RestaurantContext(restaurant);
    this.sidebarModules = Module.sidebarModules(account.getPlan());
    this.footerModules = Module.footerModules(account.getPlan());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + this.user.getRole()));
  }

  @Override
  public @Nullable String getPassword() {
    return this.user.getPassword();
  }

  @Override
  public String getUsername() {
    return this.user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.active;
  }

  public UUID getId() {
    return id;
  }

  public boolean isActive() {
    return active;
  }

  public Plan getPlan() {
    return plan;
  }

  public UserContext getUser() {
    return user;
  }

  public RestaurantContext getRestaurant() {
    return restaurant;
  }

  public List<Module> getSidebarModules() {
    return sidebarModules;
  }

  public List<Module> getFooterModules() {
    return footerModules;
  }
}

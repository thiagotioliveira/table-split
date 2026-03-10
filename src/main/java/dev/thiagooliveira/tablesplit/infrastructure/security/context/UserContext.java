package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserContext implements UserDetails {
  private final UUID id;
  private final UUID accountId;
  private final String firstName;
  private final String lastName;
  private final String name;
  private final String email;
  private final String password;
  private final String role;
  private final RestaurantContext restaurant;

  public UserContext(
      UUID accountId,
      UUID id,
      String firstName,
      String lastName,
      String email,
      String password,
      RestaurantContext restaurant) {
    this.accountId = accountId;
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.name = String.format("%s %s", this.firstName, this.lastName);
    this.email = email;
    this.password = password;
    this.role = "RESTAURANT_ADMIN";
    this.restaurant = restaurant;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
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
    return true;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public RestaurantContext getRestaurant() {
    return restaurant;
  }
}

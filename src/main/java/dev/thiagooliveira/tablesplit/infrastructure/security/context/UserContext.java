package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserContext implements UserDetails {
  private final UUID id;
  private final UUID accountId;
  private String firstName;
  private String lastName;
  private String email;
  private Language language;
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
      Language language,
      RestaurantContext restaurant) {
    this.accountId = accountId;
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.language = language;
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

  public Language getLanguage() {
    return language;
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

  public String getRole() {
    return role;
  }

  public RestaurantContext getRestaurant() {
    return restaurant;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }
}

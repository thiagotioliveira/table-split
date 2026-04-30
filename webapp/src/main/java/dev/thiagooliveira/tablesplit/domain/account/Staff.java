package dev.thiagooliveira.tablesplit.domain.account;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.StaffUpdatedEvent;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Staff extends AggregateRoot {
  private UUID id;
  private UUID restaurantId;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String password;
  private Language language;
  private Role role;
  private boolean enabled;
  private Set<Module> modules;

  // Transient accountId used for event publishing
  private transient UUID accountId;

  public void update(
      String firstName,
      String lastName,
      String email,
      String phone,
      boolean enabled,
      Set<Module> modules,
      String password) {

    var oldModules = this.modules;
    var newModules = modules;

    var addedModules =
        newModules.stream().filter(m -> !oldModules.contains(m)).collect(Collectors.toSet());

    var removedModules =
        oldModules.stream().filter(m -> !newModules.contains(m)).collect(Collectors.toSet());

    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.enabled = enabled;
    this.modules = modules;

    if (password != null && !password.isBlank()) {
      this.password = password;
    }

    this.registerEvent(
        new StaffUpdatedEvent(this.id, this.restaurantId, null, addedModules, removedModules));
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
    // Update existing events if any
    this.getDomainEvents().stream()
        .filter(e -> e instanceof StaffUpdatedEvent)
        .map(e -> (StaffUpdatedEvent) e)
        .forEach(e -> e.setAccountId(accountId));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Set<Module> getModules() {
    return modules;
  }

  public void setModules(Set<Module> modules) {
    this.modules = modules;
  }
}

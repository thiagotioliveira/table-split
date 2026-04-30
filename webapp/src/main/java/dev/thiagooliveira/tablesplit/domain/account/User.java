package dev.thiagooliveira.tablesplit.domain.account;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.PasswordUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.UserCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.UserUpdatedEvent;
import java.util.UUID;

public class User extends AggregateRoot {
  private UUID id;
  private UUID accountId;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private Language language;
  private String password;
  private Role role;

  public static User create(
      UUID id,
      UUID accountId,
      String firstName,
      String lastName,
      String email,
      String phone,
      Language language,
      String password,
      Role role) {
    User user = new User();
    user.setId(id);
    user.setAccountId(accountId);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setPhone(phone);
    user.setLanguage(language);
    user.setPassword(password);
    user.setRole(role);
    user.registerEvent(new UserCreatedEvent(accountId, user));
    return user;
  }

  public void update(
      String firstName, String lastName, String email, String phone, Language language) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.language = language;
    this.registerEvent(new UserUpdatedEvent(this.accountId, this));
  }

  public void updatePassword(String password) {
    this.password = password;
    this.registerEvent(new PasswordUpdatedEvent(this.accountId, this.id));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
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

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}

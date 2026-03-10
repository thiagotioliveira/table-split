package dev.thiagooliveira.tablesplit.domain.security;

import java.util.UUID;

public class UserContext {
  private final UUID id;
  private final String firstName;
  private final String lastName;
  private final String email;

  public UserContext(UUID id, String firstName, String lastName, String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
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
    return String.format("%s %s", this.firstName, this.lastName);
  }

  public String getEmail() {
    return email;
  }
}

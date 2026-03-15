package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
import java.util.UUID;

public class UserModel {

  private final UUID id;
  private final String firstName;
  private final String lastName;
  private final String email;

  public UserModel(UserContext user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
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

  public String getEmail() {
    return email;
  }
}

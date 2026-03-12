package dev.thiagooliveira.tablesplit.infrastructure.web.account.model;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateUserCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserProfileModel {
  @NotBlank
  @Size(min = 3, max = 255)
  private String firstName;

  @NotBlank
  @Size(min = 3, max = 255)
  private String lastName;

  @NotBlank @Email private String email;
  @NotBlank private String language;

  public UserProfileModel() {}

  public UserProfileModel(UserContext userContext) {
    this.firstName = userContext.getFirstName();
    this.lastName = userContext.getLastName();
    this.email = userContext.getEmail();
    this.language = userContext.getLanguage().name();
  }

  public UpdateUserCommand toCommand() {
    return new UpdateUserCommand(
        this.firstName, this.lastName, this.email, Language.valueOf(this.language));
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

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}

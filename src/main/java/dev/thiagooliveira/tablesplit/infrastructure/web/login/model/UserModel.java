package dev.thiagooliveira.tablesplit.infrastructure.web.login.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateUserCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserModel {
  @NotBlank private String firstName;
  @NotBlank private String lastName;
  @NotBlank private String email;
  private String phone;

  @NotBlank
  @Size(min = 8)
  private String password;

  @NotBlank private String language;

  public CreateUserCommand toCommand(PasswordEncoder passwordEncoder) {
    return new CreateUserCommand(
        this.firstName,
        this.lastName,
        this.email,
        this.phone,
        passwordEncoder.encode(this.password),
        Language.valueOf(this.language));
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

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}

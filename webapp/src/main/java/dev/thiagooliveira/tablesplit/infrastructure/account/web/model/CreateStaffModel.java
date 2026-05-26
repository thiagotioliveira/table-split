package dev.thiagooliveira.tablesplit.infrastructure.account.web.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateStaffModel {

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @NotBlank @Email private String email;

  private String phone;

  private String password;

  private boolean enabled = true;

  @NotEmpty(message = "error.staff.modules.required")
  private Set<Module> modules = new HashSet<>();

  public CreateStaffModel() {}

  public CreateStaffModel(String email) {
    this.email = email;
  }

  public CreateStaffModel(
      String firstName, String lastName, String email, String phone, Set<Module> modules) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.modules = modules;
  }

  public CreateStaffCommand toCreateCommand(
      UUID restaurantId, Language defaultLanguage, PasswordEncoder passwordEncoder) {
    return new CreateStaffCommand(
        restaurantId,
        firstName,
        lastName,
        email,
        phone,
        passwordEncoder.encode(password),
        defaultLanguage,
        modules);
  }

  // Getters and Setters
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

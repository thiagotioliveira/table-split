package dev.thiagooliveira.tablesplit.infrastructure.web.manager.staff.model;

import dev.thiagooliveira.tablesplit.application.account.command.CreateStaffCommand;
import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

public class StaffModel {
  private String id;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @NotBlank @Email private String email;

  private String phone;

  private String password;

  private boolean enabled = true;

  private Set<Module> modules = new HashSet<>();

  public StaffModel() {}

  public StaffModel(Staff staff) {
    this.id = staff.getId().toString();
    this.firstName = staff.getFirstName();
    this.lastName = staff.getLastName();
    this.email = staff.getEmail();
    this.phone = staff.getPhone();
    this.enabled = staff.isEnabled();
    this.modules = staff.getModules();
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

  public UpdateStaffCommand toUpdateCommand(PasswordEncoder passwordEncoder) {
    return new UpdateStaffCommand(
        UUID.fromString(id),
        firstName,
        lastName,
        email,
        phone,
        (password != null && !password.isBlank()) ? passwordEncoder.encode(password) : null,
        enabled,
        modules);
  }

  // Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

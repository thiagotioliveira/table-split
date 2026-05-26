package dev.thiagooliveira.tablesplit.infrastructure.account.web.model;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdateStaffModel {
  private String id;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  private String phone;

  private String password;

  private boolean enabled = true;

  @NotEmpty(message = "error.staff.modules.required")
  private Set<Module> modules = new HashSet<>();

  public UpdateStaffModel() {}

  public UpdateStaffCommand toUpdateCommand(PasswordEncoder passwordEncoder) {
    return new UpdateStaffCommand(
        UUID.fromString(id),
        firstName,
        lastName,
        null, // Email is intentionally null because it cannot be updated
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

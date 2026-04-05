package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Set;
import java.util.UUID;

public class UserContext {
  private final UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Language language;
  private String password;
  private final Role role;
  private Set<Module> modules;

  public UserContext(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.language = user.getLanguage();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.role = user.getRole();
    this.modules = Set.of(Module.values());
  }

  public UserContext(Staff staff) {
    this.id = staff.getId();
    this.firstName = staff.getFirstName();
    this.lastName = staff.getLastName();
    this.language = staff.getLanguage();
    this.email = staff.getEmail();
    this.password = staff.getPassword();
    this.role = staff.getRole();
    this.modules = staff.getModules();
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

  public Role getRole() {
    return role;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Module> getModules() {
    return modules;
  }

  public void setModules(Set<Module> modules) {
    this.modules = modules;
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID accountId;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  private String phone;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Language language;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  public static UserEntity fromDomain(User domain) {
    var entity = new UserEntity();
    entity.setId(domain.getId());
    entity.setEmail(domain.getEmail());
    entity.setAccountId(domain.getAccountId());
    entity.setFirstName(domain.getFirstName());
    entity.setLastName(domain.getLastName());
    entity.setPhone(domain.getPhone());
    entity.setLanguage(domain.getLanguage());
    entity.setPassword(domain.getPassword());
    entity.setRole(domain.getRole());
    return entity;
  }

  public User toDomain() {
    var user = new User();
    user.setAccountId(this.accountId);
    user.setPhone(this.phone);
    user.setEmail(this.email);
    user.setLastName(this.lastName);
    user.setFirstName(this.firstName);
    user.setId(this.id);
    user.setPassword(this.getPassword());
    user.setLanguage(this.getLanguage());
    user.setRole(this.role);
    return user;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
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
}

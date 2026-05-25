package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pending_registrations")
public class PendingRegistrationEntity {

  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, length = 6)
  private String code;

  @Column(name = "registration_data", nullable = false, columnDefinition = "TEXT")
  private String registrationData;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(nullable = false, length = 5)
  private String language = "PT";

  public PendingRegistrationEntity() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getRegistrationData() {
    return registrationData;
  }

  public void setRegistrationData(String registrationData) {
    this.registrationData = registrationData;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}

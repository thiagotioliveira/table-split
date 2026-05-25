package dev.thiagooliveira.tablesplit.domain.account;

import java.time.LocalDateTime;
import java.util.UUID;

public class PendingRegistration {
  private UUID id;
  private String email;
  private String code;
  private String registrationData;
  private LocalDateTime expiresAt;
  private String language = "PT";

  public PendingRegistration() {}

  public PendingRegistration(
      UUID id, String email, String code, String registrationData, LocalDateTime expiresAt) {
    this(id, email, code, registrationData, expiresAt, "PT");
  }

  public PendingRegistration(
      UUID id,
      String email,
      String code,
      String registrationData,
      LocalDateTime expiresAt,
      String language) {
    this.id = id;
    this.email = email;
    this.code = code;
    this.registrationData = registrationData;
    this.expiresAt = expiresAt;
    this.language = language;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

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

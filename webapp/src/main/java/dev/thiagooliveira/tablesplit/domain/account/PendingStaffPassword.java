package dev.thiagooliveira.tablesplit.domain.account;

import java.time.LocalDateTime;
import java.util.UUID;

public class PendingStaffPassword {
  private UUID id;
  private String email;
  private LocalDateTime expiresAt;

  public PendingStaffPassword() {}

  public PendingStaffPassword(UUID id, String email, LocalDateTime expiresAt) {
    this.id = id;
    this.email = email;
    this.expiresAt = expiresAt;
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

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }
}

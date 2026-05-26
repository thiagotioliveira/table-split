package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pending_user_password_resets", schema = "public")
public class PendingUserPasswordResetEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  public PendingUserPasswordResetEntity() {}

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

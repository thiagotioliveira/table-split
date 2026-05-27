package dev.thiagooliveira.tablesplit.domain.account;

import java.time.LocalDateTime;
import java.util.UUID;

public class PendingAccountCancellation {
  private UUID id;
  private UUID accountId;
  private String code;
  private LocalDateTime expiresAt;

  public PendingAccountCancellation() {}

  public PendingAccountCancellation(UUID id, UUID accountId, String code, LocalDateTime expiresAt) {
    this.id = id;
    this.accountId = accountId;
    this.code = code;
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

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }
}

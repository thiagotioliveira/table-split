package dev.thiagooliveira.tablesplit.domain.account;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Account {
  private UUID id;
  private OffsetDateTime createdAt;
  private boolean active;
  private Plan plan;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Plan getPlan() {
    return plan;
  }

  public void setPlan(Plan plan) {
    this.plan = plan;
  }
}

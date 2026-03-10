package dev.thiagooliveira.tablesplit.domain.dashboard;

import dev.thiagooliveira.tablesplit.domain.dashboard.v1.DefaultDashboardAttributes;
import java.util.UUID;

public class Dashboard {
  private UUID id;
  private UUID accountId;
  private UUID userId;
  private DefaultDashboardAttributes attributes;

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

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public DefaultDashboardAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(DefaultDashboardAttributes attributes) {
    this.attributes = attributes;
  }
}

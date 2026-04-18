package dev.thiagooliveira.tablesplit.domain.account;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Account {
  private UUID id;
  private OffsetDateTime createdAt;
  private Plan plan;
  private AccountStatus status = AccountStatus.ACTIVE;
  private OffsetDateTime trialStartedAt;
  private OffsetDateTime trialEndsAt;

  public void startTrial() {
    if (trialStartedAt != null) {
      throw new IllegalStateException("error.account.trial_already_used");
    }
    this.status = AccountStatus.TRIAL;
    this.trialStartedAt = Time.nowOffset();
    this.trialEndsAt = trialStartedAt.plusDays(30);
  }

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
    return status == AccountStatus.ACTIVE || status == AccountStatus.TRIAL;
  }

  public Plan getPlan() {
    return plan;
  }

  public void setPlan(Plan plan) {
    this.plan = plan;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  public boolean isTrialUsed() {
    return trialStartedAt != null;
  }

  public OffsetDateTime getTrialStartedAt() {
    return trialStartedAt;
  }

  public void setTrialStartedAt(OffsetDateTime trialStartedAt) {
    this.trialStartedAt = trialStartedAt;
  }

  public OffsetDateTime getTrialEndsAt() {
    return trialEndsAt;
  }

  public void setTrialEndsAt(OffsetDateTime trialEndsAt) {
    this.trialEndsAt = trialEndsAt;
  }

  public boolean isInTrial() {
    return status == AccountStatus.TRIAL
        && trialEndsAt != null
        && Time.nowOffset().isBefore(trialEndsAt);
  }

  public Plan getEffectivePlan() {
    if (isInTrial()) {
      return Plan.PROFESSIONAL;
    }
    return plan;
  }
}

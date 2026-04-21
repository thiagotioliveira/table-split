package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountStatus;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Plan plan;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AccountStatus status;

  private OffsetDateTime trialStartedAt;

  private OffsetDateTime trialEndsAt;

  public static AccountEntity fromDomain(Account domain) {
    var entity = new AccountEntity();
    entity.setId(domain.getId());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setPlan(domain.getPlan());
    entity.setStatus(domain.getStatus());
    entity.setTrialStartedAt(domain.getTrialStartedAt());
    entity.setTrialEndsAt(domain.getTrialEndsAt());
    return entity;
  }

  public Account toDomain() {
    var domain = new Account();
    domain.setId(this.id);
    domain.setCreatedAt(this.createdAt);
    domain.setPlan(this.plan);
    domain.setStatus(this.status);
    domain.setTrialStartedAt(this.trialStartedAt);
    domain.setTrialEndsAt(this.trialEndsAt);
    return domain;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AccountEntity that = (AccountEntity) o;
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

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
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
}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import dev.thiagooliveira.tablesplit.domain.account.Account;
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
  private boolean active;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Plan plan;

  public static AccountEntity fromDomain(Account domain) {
    var entity = new AccountEntity();
    entity.setId(domain.getId());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setActive(domain.isActive());
    entity.setPlan(domain.getPlan());
    return entity;
  }

  public Account toDomain() {
    var domain = new Account();
    domain.setId(this.id);
    domain.setActive(this.active);
    domain.setCreatedAt(this.createdAt);
    domain.setPlan(this.plan);
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

package dev.thiagooliveira.tablesplit.infrastructure.persistence.dashboard;

import dev.thiagooliveira.tablesplit.domain.dashboard.Dashboard;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.DefaultDashboardAttributes;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "dashboards")
public class DashboardEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private UUID accountId;

  @Column(nullable = false, unique = true)
  private UUID userId;

  @Column(nullable = false)
  private String contentType;

  @Convert(converter = DefaultDashboardAttributesConverter.class)
  @Column(nullable = false, columnDefinition = "TEXT")
  private DefaultDashboardAttributes attributes;

  public static DashboardEntity fromDomain(Dashboard domain) {
    var entity = new DashboardEntity();
    entity.setId(domain.getId());
    entity.setAccountId(domain.getAccountId());
    entity.setUserId(domain.getUserId());
    entity.setContentType(domain.getAttributes().getClass().getName());
    entity.setAttributes(domain.getAttributes());
    return entity;
  }

  public Dashboard toDomain() {
    var domain = new Dashboard();
    domain.setId(this.id);
    domain.setAccountId(this.accountId);
    domain.setUserId(this.userId);
    domain.setAttributes(this.attributes);
    return domain;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    DashboardEntity that = (DashboardEntity) o;
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

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public DefaultDashboardAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(DefaultDashboardAttributes attributes) {
    this.attributes = attributes;
  }
}

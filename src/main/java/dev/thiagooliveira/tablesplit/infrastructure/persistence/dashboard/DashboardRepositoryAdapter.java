package dev.thiagooliveira.tablesplit.infrastructure.persistence.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.domain.dashboard.Dashboard;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DashboardRepositoryAdapter implements DashboardRepository {

  private final DashboardJpaRepository dashboardJpaRepository;

  public DashboardRepositoryAdapter(DashboardJpaRepository dashboardJpaRepository) {
    this.dashboardJpaRepository = dashboardJpaRepository;
  }

  @Override
  public List<Dashboard> findByAccountId(UUID accountId) {
    return this.dashboardJpaRepository.findByAccountId(accountId).stream()
        .map(DashboardEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Dashboard> findByUserId(UUID userId) {
    return this.dashboardJpaRepository.findByUserId(userId).map(DashboardEntity::toDomain);
  }

  @Override
  public void save(Dashboard dashboard) {
    this.dashboardJpaRepository.save(DashboardEntity.fromDomain(dashboard));
  }
}

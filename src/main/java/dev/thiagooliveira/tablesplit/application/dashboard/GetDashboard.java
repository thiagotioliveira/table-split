package dev.thiagooliveira.tablesplit.application.dashboard;

import dev.thiagooliveira.tablesplit.domain.dashboard.Dashboard;
import java.util.Optional;
import java.util.UUID;

public class GetDashboard {

  private final DashboardRepository dashboardRepository;

  public GetDashboard(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  public Optional<Dashboard> execute(UUID userId) {
    return this.dashboardRepository.findByUserId(userId);
  }
}

package dev.thiagooliveira.tablesplit.application.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.command.UpdateDashboardCommand;
import java.util.UUID;

public class UpdateDashboard {

  private final DashboardRepository dashboardRepository;

  public UpdateDashboard(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  public void execute(UUID userId, UpdateDashboardCommand command) {
    // TODO
  }
}

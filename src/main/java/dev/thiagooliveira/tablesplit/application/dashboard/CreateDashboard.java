package dev.thiagooliveira.tablesplit.application.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.command.CreateDashboardCommand;
import dev.thiagooliveira.tablesplit.domain.dashboard.Dashboard;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.DefaultDashboardAttributes;
import java.util.UUID;

public class CreateDashboard {

  private final DashboardRepository dashboardRepository;

  public CreateDashboard(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  public void execute(UUID accountId, UUID userId, CreateDashboardCommand command) {
    if (this.dashboardRepository.findByUserId(userId).isPresent()) {
      throw new RuntimeException(); // TODO
    }
    var dashboard = new Dashboard();
    dashboard.setId(UUID.randomUUID());
    dashboard.setAccountId(accountId);
    dashboard.setUserId(userId);
    dashboard.setAttributes(new DefaultDashboardAttributes());
    dashboard.getAttributes().setUserFirstName(command.userFirstName());
    this.dashboardRepository.save(dashboard);
  }
}

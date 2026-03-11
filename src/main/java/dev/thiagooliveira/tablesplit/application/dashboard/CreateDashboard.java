package dev.thiagooliveira.tablesplit.application.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.command.CreateDashboardCommand;
import dev.thiagooliveira.tablesplit.domain.dashboard.Dashboard;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.DefaultDashboardAttributes;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.UserAttributes;
import java.util.UUID;

public class CreateDashboard {

  private final DashboardRepository dashboardRepository;

  public CreateDashboard(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  public void execute(CreateDashboardCommand command) {
    if (this.dashboardRepository.findByUserId(command.userId()).isPresent()) {
      throw new RuntimeException(); // TODO
    }
    var dashboard = new Dashboard();
    dashboard.setId(UUID.randomUUID());
    dashboard.setAccountId(command.accountId());
    dashboard.setUserId(command.userId());
    dashboard.setAttributes(
        new DefaultDashboardAttributes(
            new UserAttributes(
                command.userId(), command.firstName(), command.lastName(), command.email())));
    this.dashboardRepository.save(dashboard);
  }
}

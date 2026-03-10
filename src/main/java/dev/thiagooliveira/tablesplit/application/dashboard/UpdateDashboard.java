package dev.thiagooliveira.tablesplit.application.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.command.UpdateDashboardCommand;
import java.util.UUID;

public class UpdateDashboard {

  private final DashboardRepository dashboardRepository;

  public UpdateDashboard(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  public void execute(UUID userId, UpdateDashboardCommand command) {
    var dashboard = this.dashboardRepository.findByUserId(userId).orElseThrow(); // TODO
    dashboard.getAttributes().setRestaurantName(command.restaurantName());
    dashboard.getAttributes().setRestaurantAddress(command.restaurantAddress());
    dashboard.getAttributes().setRestaurantSlug(command.restaurantSlug());
    this.dashboardRepository.save(dashboard);
  }
}

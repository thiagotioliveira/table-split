package dev.thiagooliveira.tablesplit.infrastructure.config.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.CreateDashboard;
import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.application.dashboard.GetDashboard;
import dev.thiagooliveira.tablesplit.application.dashboard.UpdateDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashboardConfig {

  @Bean
  public CreateDashboard createDashboard(DashboardRepository dashboardRepository) {
    return new CreateDashboard(dashboardRepository);
  }

  @Bean
  public UpdateDashboard updateDashboard(DashboardRepository dashboardRepository) {
    return new UpdateDashboard(dashboardRepository);
  }

  @Bean
  public GetDashboard getDashboard(DashboardRepository dashboardRepository) {
    return new GetDashboard(dashboardRepository);
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.listener.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.CategoryAttributes;
import dev.thiagooliveira.tablesplit.domain.event.CategoryCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.CategoryDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.CategoryUpdatedEvent;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CategoryEventListener {

  private final DashboardRepository dashboardRepository;

  public CategoryEventListener(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  @EventListener
  public void on(CategoryCreatedEvent event) {
    this.updateDashboard(event.getAccountId(), event.getDetails().getTotal());
  }

  @EventListener
  public void on(CategoryDeletedEvent event) {
    this.updateDashboard(event.getAccountId(), event.getDetails().getTotal());
  }

  @EventListener
  public void on(CategoryUpdatedEvent event) {
    this.updateDashboard(event.getAccountId(), event.getDetails().getTotal());
  }

  private void updateDashboard(UUID accountId, long totalCategories) {
    var dashboards = this.dashboardRepository.findByAccountId(accountId);
    dashboards.forEach(
        d -> {
          d.getAttributes()
              .setCategories(new CategoryAttributes(totalCategories, totalCategories, 0L));
          this.dashboardRepository.save(d);
        });
  }
}

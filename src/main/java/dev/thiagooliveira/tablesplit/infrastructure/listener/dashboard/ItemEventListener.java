package dev.thiagooliveira.tablesplit.infrastructure.listener.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.domain.event.ItemCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemUpdatedEvent;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ItemEventListener {
  private final DashboardRepository dashboardRepository;

  public ItemEventListener(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  @EventListener
  public void on(ItemCreatedEvent event) {
    this.updateDashboard(
        event.getAccountId(),
        event.getDetails().getTotal(),
        event.getDetails().getTotalActive(),
        event.getDetails().getTotalInactive());
  }

  @EventListener
  public void on(ItemDeletedEvent event) {
    this.updateDashboard(
        event.getAccountId(),
        event.getDetails().getTotal(),
        event.getDetails().getTotalActive(),
        event.getDetails().getTotalInactive());
  }

  @EventListener
  public void on(ItemUpdatedEvent event) {
    this.updateDashboard(
        event.getAccountId(),
        event.getDetails().getTotal(),
        event.getDetails().getTotalActive(),
        event.getDetails().getTotalInactive());
  }

  private void updateDashboard(
      UUID accountId, long totalItems, long totalItemsActive, long totalItemsInactive) {
    var dashboards = this.dashboardRepository.findByAccountId(accountId);
    dashboards.forEach(
        d -> {
          d.getAttributes().setTotalItems(totalItems);
          d.getAttributes().setTotalItemsActive(totalItemsActive);
          d.getAttributes().setTotalItemsInactive(totalItemsInactive);
          this.dashboardRepository.save(d);
        });
  }
}

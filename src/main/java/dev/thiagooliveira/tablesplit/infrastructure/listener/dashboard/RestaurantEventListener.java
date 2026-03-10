package dev.thiagooliveira.tablesplit.infrastructure.listener.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantUpdatedEvent;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RestaurantEventListener {

  private final DashboardRepository dashboardRepository;

  public RestaurantEventListener(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  @EventListener
  public void on(RestaurantCreatedEvent event) {
    this.updateDashboard(
        event.getAccountId(),
        event.getDetails().getName(),
        event.getDetails().getAddress(),
        event.getDetails().getSlug());
  }

  @EventListener
  public void on(RestaurantUpdatedEvent event) {
    this.updateDashboard(
        event.getAccountId(),
        event.getDetails().getName(),
        event.getDetails().getAddress(),
        event.getDetails().getSlug());
  }

  private void updateDashboard(
      UUID accountId, String restaurantName, String restaurantAddress, String restaurantSlug) {
    this.dashboardRepository
        .findByAccountId(accountId)
        .forEach(
            d -> {
              d.getAttributes().setRestaurantName(restaurantName);
              d.getAttributes().setRestaurantAddress(restaurantAddress);
              d.getAttributes().setRestaurantSlug(restaurantSlug);
              this.dashboardRepository.save(d);
            });
  }
}

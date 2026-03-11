package dev.thiagooliveira.tablesplit.infrastructure.listener.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.RestaurantAttributes;
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
        event.getRestaurantId(),
        event.getDetails().getName(),
        event.getDetails().getAddress(),
        event.getDetails().getSlug(),
        event.getDetails().getCurrency().getSymbol());
  }

  @EventListener
  public void on(RestaurantUpdatedEvent event) {
    this.updateDashboard(
        event.getAccountId(),
        event.getRestaurantId(),
        event.getDetails().getName(),
        event.getDetails().getAddress(),
        event.getDetails().getSlug(),
        event.getDetails().getCurrency().getSymbol());
  }

  private void updateDashboard(
      UUID accountId,
      UUID restaurantId,
      String restaurantName,
      String restaurantAddress,
      String restaurantSlug,
      String currencySymbol) {
    this.dashboardRepository
        .findByAccountId(accountId)
        .forEach(
            d -> {
              d.getAttributes()
                  .setRestaurant(
                      new RestaurantAttributes(
                          restaurantId,
                          restaurantName,
                          restaurantAddress,
                          restaurantSlug,
                          currencySymbol));
              this.dashboardRepository.save(d);
            });
  }
}

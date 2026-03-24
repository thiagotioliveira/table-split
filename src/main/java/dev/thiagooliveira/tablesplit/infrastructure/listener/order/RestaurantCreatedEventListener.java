package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RestaurantCreatedEventListener {

  private final CreateTable createTable;

  public RestaurantCreatedEventListener(CreateTable createTable) {
    this.createTable = createTable;
  }

  @EventListener
  public void on(RestaurantCreatedEvent event) {
    if (event.getNumberOfTables() > 0) {
      for (int i = 0; i < event.getNumberOfTables(); i++) {
        this.createTable.execute(event.getRestaurantId(), String.format("%02d", i + 1));
      }
    }
  }
}

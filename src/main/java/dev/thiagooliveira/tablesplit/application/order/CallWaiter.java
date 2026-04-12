package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.WaiterCalledEvent;
import java.util.UUID;

public class CallWaiter {

  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;

  public CallWaiter(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      EventPublisher eventPublisher) {
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID restaurantId, String tableCod) {
    execute(restaurantId, tableCod, null);
  }

  public void execute(UUID restaurantId, String tableCod, UUID customerId) {
    var table =
        tableRepository
            .findByRestaurantIdAndCod(restaurantId, tableCod)
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableCod));

    if (table.isAvailable() && customerId != null) {
      orderRepository.findAllByTableIdOrderByOpenedAtDesc(table.getId()).stream()
          .findFirst()
          .ifPresent(
              lastOrder -> {
                if (lastOrder.getStatus()
                    == dev.thiagooliveira.tablesplit.domain.order.OrderStatus.CLOSED) {
                  if (lastOrder.getCustomers().stream()
                      .anyMatch(c -> c.getId().equals(customerId))) {
                    throw new TableSessionClosedException(
                        "Table is closed. Please complete your feedback.");
                  }
                }
              });
    }

    eventPublisher.publishEvent(new WaiterCalledEvent(restaurantId, tableCod));
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.order.listener;

import dev.thiagooliveira.tablesplit.application.notification.Broadcaster;
import dev.thiagooliveira.tablesplit.domain.order.event.OrderClosedEvent;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

  private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

  private final Broadcaster broadcaster;
  private final RestaurantRepository restaurantRepository;

  public OrderEventListener(Broadcaster broadcaster, RestaurantRepository restaurantRepository) {
    this.broadcaster = broadcaster;
    this.restaurantRepository = restaurantRepository;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleOrderClosed(OrderClosedEvent event) {
    logger.debug("Handling OrderClosedEvent for restaurant: {}", event.getRestaurantId());

    // Send Push Notification
    try {
      restaurantRepository
          .findById(event.getRestaurantId())
          .ifPresent(
              restaurant -> {
                broadcaster.orderClosed(
                    event.getRestaurantId(),
                    event.getTableCod(),
                    event.getTotalAmount(),
                    restaurant.getCurrency(),
                    event.getInitiatedBy());
              });
    } catch (Exception e) {
      logger.error("Error sending order closed notification", e);
    }
  }
}

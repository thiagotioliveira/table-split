package dev.thiagooliveira.tablesplit.infrastructure.order.listener;

import dev.thiagooliveira.tablesplit.domain.order.event.TicketCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

public class TicketMessagingListener {

  private static final Logger log = LoggerFactory.getLogger(TicketMessagingListener.class);
  private final RabbitTemplate rabbitTemplate;
  private final String exchangeName;
  private final TicketMessageMapper ticketMessageMapper;

  public TicketMessagingListener(
      RabbitTemplate rabbitTemplate,
      @Value("${app.integration.rabbit.order-ticket-exchange}") String exchangeName,
      TicketMessageMapper ticketMessageMapper) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchangeName = exchangeName;
    this.ticketMessageMapper = ticketMessageMapper;
    log.info("Service initialized and ready to dispatch orders to exchange: {}", exchangeName);
  }

  @EventListener
  public void handleTicketCreated(TicketCreatedEvent event) {
    log.info("Dispatching ticket {} to POS integration queue", event.getTicket().getId());

    try {
      // We use a routing key based on restaurantId so the agent can filter
      String routingKey = "restaurant." + event.getRestaurantId() + ".orders";

      rabbitTemplate.convertAndSend(exchangeName, routingKey, ticketMessageMapper.toMessage(event));

      log.debug(
          "Ticket {} dispatched successfully with routing key {}",
          event.getTicket().getId(),
          routingKey);
    } catch (Exception e) {
      log.error(
          "Failed to dispatch ticket {} to POS integration. Error: {}",
          event.getTicket().getId(),
          e.getMessage(),
          e);
    }
  }
}

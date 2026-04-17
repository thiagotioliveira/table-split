package dev.thiagooliveira.tablesplit.infrastructure.messaging.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.infrastructure.messaging.order.model.IntegrationOrderDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class OrderDispatcher {

  private static final Logger log = LoggerFactory.getLogger(OrderDispatcher.class);
  private final RabbitTemplate rabbitTemplate;

  public OrderDispatcher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
    log.info("OrderDispatcher: Service initialized and ready to dispatch orders.");
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleTicketCreated(TicketCreatedEvent event) {
    log.info("Dispatching ticket {} to POS integration queue", event.getTicket().getId());

    try {
      IntegrationOrderDTO dto = mapToIntegrationDto(event);

      // We use a routing key based on restaurantId so the agent can filter
      String routingKey = "restaurant." + event.getRestaurantId() + ".orders";

      rabbitTemplate.convertAndSend("order.integration.exchange", routingKey, dto);

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

  private IntegrationOrderDTO mapToIntegrationDto(TicketCreatedEvent event) {
    Ticket ticket = event.getTicket();

    List<IntegrationOrderDTO.Item> items =
        ticket.getItems().stream()
            .map(
                item -> {
                  String name =
                      item.getName()
                          .getOrDefault(
                              Language.PT, item.getName().getOrDefault(Language.EN, "Item"));

                  return new IntegrationOrderDTO.Item(
                      item.getId(),
                      name,
                      item.getQuantity(),
                      item.getUnitPrice(),
                      item.getTotalPrice(),
                      item.getNote());
                })
            .toList();

    return new IntegrationOrderDTO(
        ticket.getId(),
        event.getTableCod(),
        event
            .getOrder()
            .getCustomerName(
                ticket
                    .getItems()
                    .get(0)
                    .getCustomerId()), // Passing the customer ID from the first item
        items,
        ticket.calculateTotal(),
        ticket.getCreatedAt());
  }
}

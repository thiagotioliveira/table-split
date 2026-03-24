package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTicketItemStatus {

  private final OrderRepository orderRepository;

  public UpdateTicketItemStatus(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional
  public void execute(UUID itemId, TicketStatus newStatus) {
    Order order =
        orderRepository
            .findByTicketItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

    order
        .getTickets()
        .forEach(
            ticket -> {
              ticket.getItems().stream()
                  .filter(item -> item.getId().equals(itemId))
                  .findFirst()
                  .ifPresent(
                      item -> {
                        item.setStatus(newStatus);
                        ticket.recalculateStatus();
                      });
            });

    orderRepository.save(order);
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.order.listener;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.order.listener.model.TicketCreatedMessage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TicketMessageMapper {

  public TicketCreatedMessage toMessage(TicketCreatedEvent event) {
    Ticket ticket = event.getTicket();

    List<TicketCreatedMessage.Item> items =
        ticket.getItems().stream()
            .map(
                item -> {
                  String name =
                      item.getName()
                          .getOrDefault(
                              event.getLanguage(),
                              item.getName().getOrDefault(Language.PT, "Item"));

                  return new TicketCreatedMessage.Item(
                      item.getId(),
                      name,
                      item.getQuantity(),
                      item.getUnitPrice(),
                      item.getTotalPrice(),
                      item.getNote(),
                      ticket.getNote());
                })
            .toList();

    return new TicketCreatedMessage(
        ticket.getId(),
        event.getTableCod(),
        event
            .getOrder()
            .getCustomerName(
                ticket
                    .getItems()
                    .get(0)
                    .getCustomerId()), // Passing the customer ID from the first item
        ticket.getNote(),
        items,
        ticket.calculateTotal(),
        ticket.getCreatedAt());
  }
}

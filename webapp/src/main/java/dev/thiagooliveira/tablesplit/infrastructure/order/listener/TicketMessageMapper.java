package dev.thiagooliveira.tablesplit.infrastructure.order.listener;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.order.listener.model.TicketCreatedMessage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TicketMessageMapper {

  @org.springframework.beans.factory.annotation.Autowired
  protected org.springframework.context.MessageSource messageSource;

  protected String resolveCustomerName(
      dev.thiagooliveira.tablesplit.domain.order.Order order,
      java.util.UUID customerId,
      Language userLanguage) {

    java.util.Optional<String> nameOpt = order.getCustomerName(customerId);
    if (nameOpt.isPresent()) {
      return nameOpt.get();
    }

    java.util.Locale locale =
        userLanguage != null
            ? java.util.Locale.forLanguageTag(userLanguage.getLabel())
            : org.springframework.context.i18n.LocaleContextHolder.getLocale();

    if (customerId == null) {
      if (order.getTableId() == null) {
        return messageSource.getMessage("customer.anonymous.takeaway", null, "Balcão", locale);
      } else {
        return messageSource.getMessage("customer.anonymous.table", null, "Mesa", locale);
      }
    } else {
      return messageSource.getMessage("customer.anonymous.unknown", null, "Desconhecido", locale);
    }
  }

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
        resolveCustomerName(
            event.getOrder(),
            ticket.getItems().isEmpty() ? null : ticket.getItems().get(0).getCustomerId(),
            event.getLanguage()),
        ticket.getNote(),
        items,
        ticket.calculateTotal(),
        ticket.getCreatedAt());
  }
}

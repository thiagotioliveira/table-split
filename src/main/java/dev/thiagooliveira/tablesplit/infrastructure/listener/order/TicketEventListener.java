package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketModel;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  public TicketEventListener(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @EventListener
  public void handleTicketCreated(TicketCreatedEvent event) {
    TicketModel model = mapToModel(event.getTicket(), event.getTableCod());
    messagingTemplate.convertAndSend(
        "/topic/restaurant/" + event.getRestaurantId() + "/tickets", model);
  }

  @EventListener
  public void handleTicketStatusChanged(TicketStatusChangedEvent event) {
    messagingTemplate.convertAndSend(
        "/topic/restaurant/" + event.getRestaurantId() + "/tickets", event);
    messagingTemplate.convertAndSend(
        "/topic/restaurant/" + event.getRestaurantId() + "/tables", event);
  }

  private TicketModel mapToModel(Ticket ticket, String tableCod) {
    List<TicketItemModel> itemModels =
        ticket.getItems().stream()
            .map(
                item -> {
                  String name = "Item";
                  if (item.getName() != null && !item.getName().isEmpty()) {
                    name =
                        item.getName()
                            .getOrDefault(
                                Language.PT,
                                item.getName()
                                    .getOrDefault(
                                        Language.EN, item.getName().values().iterator().next()));
                  }
                  return new TicketItemModel(
                      item.getId(),
                      item.getCustomerName(),
                      name,
                      item.getQuantity(),
                      item.getUnitPrice(),
                      item.getTotalPrice(),
                      item.getNote(),
                      item.getStatus().getLabel(),
                      item.getStatus().getCssClass());
                })
            .toList();

    String customerName = itemModels.isEmpty() ? "Cliente" : itemModels.get(0).getCustomerName();
    if (customerName == null || customerName.isBlank()) customerName = "Mesa " + tableCod;

    long minutesAgo = Duration.between(ticket.getCreatedAt(), ZonedDateTime.now()).toMinutes();
    String timeAgo = minutesAgo == 0 ? "agora" : "há " + minutesAgo + " min";
    boolean urgent = minutesAgo > 15 && ticket.getStatus() == TicketStatus.PENDING;

    return new TicketModel(
        ticket.getId(),
        tableCod,
        customerName,
        ticket.getStatus(),
        ticket.getCreatedAt(),
        timeAgo,
        itemModels,
        ticket.calculateTotal(),
        urgent);
  }
}

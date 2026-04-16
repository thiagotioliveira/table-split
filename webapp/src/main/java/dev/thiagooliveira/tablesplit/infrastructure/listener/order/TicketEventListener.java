package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.application.notification.Broadcaster;
import dev.thiagooliveira.tablesplit.application.notification.RegisterWaiterCall;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketItemStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.WaiterCalledEvent;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketModel;
import java.time.Duration;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TicketEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final Broadcaster broadcaster;
  private final RegisterWaiterCall registerWaiterCall;
  private final dev.thiagooliveira.tablesplit.application.notification.ListActiveWaiterCalls
      listActiveWaiterCalls;

  public TicketEventListener(
      SimpMessagingTemplate messagingTemplate,
      Broadcaster broadcaster,
      RegisterWaiterCall registerWaiterCall,
      dev.thiagooliveira.tablesplit.application.notification.ListActiveWaiterCalls
          listActiveWaiterCalls) {
    this.messagingTemplate = messagingTemplate;
    this.broadcaster = broadcaster;
    this.registerWaiterCall = registerWaiterCall;
    this.listActiveWaiterCalls = listActiveWaiterCalls;
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketCreated(TicketCreatedEvent event) {
    TicketModel model = mapToModel(event.getTicket(), event.getOrder(), event.getTableCod());
    notifyRestaurant(event.getRestaurantId(), model);

    // Send Push Notification
    try {
      String payload =
          String.format(
              "{\"title\": \"Novo Pedido - Mesa %s\", \"body\": \"%s fez um novo pedido\", \"url\": \"/orders\"}",
              event.getTableCod(), model.getCustomerName());
      broadcaster.newOrder(event.getRestaurantId(), payload);
    } catch (Exception e) {
      // Silently fail push if anything goes wrong
    }
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketStatusChanged(TicketStatusChangedEvent event) {
    notifyRestaurant(event.getRestaurantId(), event);
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketItemStatusChanged(TicketItemStatusChangedEvent event) {
    notifyRestaurant(event.getRestaurantId(), event);
  }

  @EventListener
  public void handleWaiterCalled(WaiterCalledEvent event) {
    // Register the call first
    dev.thiagooliveira.tablesplit.domain.notification.WaiterCall call = null;
    try {
      call = registerWaiterCall.execute(event.getRestaurantId(), event.getTableCod());
    } catch (Exception e) {
      // Silently fail
    }

    // Now calculate count
    long count = listActiveWaiterCalls.execute(event.getRestaurantId()).size();
    WaiterCalledEvent eventWithData =
        new WaiterCalledEvent(
            event.getRestaurantId(),
            event.getTableCod(),
            count,
            call != null ? call.getId() : null);

    // Notify via WebSocket
    messagingTemplate.convertAndSend(
        "/topic/restaurant/" + event.getRestaurantId() + "/waiter", eventWithData);
  }

  @org.springframework.context.event.EventListener
  public void handleWaiterCallDismissed(
      dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent event) {
    long count = listActiveWaiterCalls.execute(event.getRestaurantId()).size();
    dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent eventWithCount =
        new dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent(
            event.getRestaurantId(), event.getCallId(), count);

    messagingTemplate.convertAndSend(
        "/topic/restaurant/" + event.getRestaurantId() + "/waiter", eventWithCount);
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTableStatusChanged(TableStatusChangedEvent event) {
    notifyRestaurant(event.getRestaurantId(), event);
  }

  private void notifyRestaurant(java.util.UUID restaurantId, Object payload) {
    messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId + "/tickets", payload);
    messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId + "/tables", payload);
  }

  private TicketModel mapToModel(
      Ticket ticket, dev.thiagooliveira.tablesplit.domain.order.Order order, String tableCod) {
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
                      item.getCustomerId(),
                      order.getCustomerName(item.getCustomerId()),
                      name,
                      item.getQuantity(),
                      item.getUnitPrice(),
                      item.getTotalPrice(),
                      item.getNote(),
                      item.getStatus().getLabel(),
                      item.getStatus().getCssClass(),
                      item.getRating(),
                      ticket.getCreatedAt(),
                      getItemPromotionInfo(item));
                })
            .toList();

    String customerName = itemModels.isEmpty() ? "Cliente" : itemModels.get(0).getCustomerName();
    if (customerName == null || customerName.isBlank()) customerName = "Mesa " + tableCod;

    long minutesAgo = Duration.between(ticket.getCreatedAt(), Time.now()).toMinutes();
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

  private TicketItemModel.PromotionInfo getItemPromotionInfo(
      dev.thiagooliveira.tablesplit.domain.order.TicketItem ticketItem) {
    if (ticketItem.getPromotionSnapshot() != null) {
      var snapshot = ticketItem.getPromotionSnapshot();
      return new TicketItemModel.PromotionInfo(
          snapshot.originalPrice(),
          ticketItem.getUnitPrice(),
          snapshot.discountType(),
          snapshot.discountValue());
    }
    return null;
  }
}

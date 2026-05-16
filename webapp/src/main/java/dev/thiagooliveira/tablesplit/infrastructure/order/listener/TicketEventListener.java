package dev.thiagooliveira.tablesplit.infrastructure.order.listener;

import dev.thiagooliveira.tablesplit.application.notification.Broadcaster;
import dev.thiagooliveira.tablesplit.application.notification.RegisterWaiterCall;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.domain.order.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TableTransferredEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TicketItemStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.TicketStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.WaiterCallDismissedEvent;
import dev.thiagooliveira.tablesplit.domain.order.event.WaiterCalledEvent;
import dev.thiagooliveira.tablesplit.infrastructure.notification.SseService;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketModel;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TicketEventListener {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(TicketEventListener.class);

  private final SseService sseService;
  private final Broadcaster broadcaster;
  private final RegisterWaiterCall registerWaiterCall;
  private final dev.thiagooliveira.tablesplit.application.notification.ListActiveWaiterCalls
      listActiveWaiterCalls;
  private final MessageSource messageSource;

  public TicketEventListener(
      SseService sseService,
      Broadcaster broadcaster,
      RegisterWaiterCall registerWaiterCall,
      dev.thiagooliveira.tablesplit.application.notification.ListActiveWaiterCalls
          listActiveWaiterCalls,
      MessageSource messageSource) {
    this.sseService = sseService;
    this.broadcaster = broadcaster;
    this.registerWaiterCall = registerWaiterCall;
    this.listActiveWaiterCalls = listActiveWaiterCalls;
    this.messageSource = messageSource;
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketCreated(TicketCreatedEvent event) {
    logger.debug("Handling TicketCreatedEvent for restaurant: {}", event.getRestaurantId());
    TicketModel model =
        mapToModel(
            event.getRestaurantId(),
            event.getTicket(),
            event.getOrder(),
            event.getTableCod(),
            event.getLanguage());

    UUID initiatedBy = event.getInitiatedBy();

    broadcast(event.getRestaurantId(), "TICKET_CREATED", model, initiatedBy);

    // Send Push Notification
    try {
      broadcaster.newOrder(
          event.getRestaurantId(), event.getTableCod(), model.getCustomerName(), initiatedBy);
    } catch (Exception e) {
      // Silently fail push if anything goes wrong
    }
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketStatusChanged(TicketStatusChangedEvent event) {
    logger.debug(
        "Handling TicketStatusChangedEvent for restaurant: {}. New status: {}",
        event.getRestaurantId(),
        event.getNewStatus());
    broadcast(
        event.getRestaurantId(),
        "TICKET_STATUS_CHANGED",
        java.util.Map.of(
            "ticketId", event.getTicketId().toString(), "status", event.getNewStatus()));
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketItemStatusChanged(TicketItemStatusChangedEvent event) {
    logger.debug(
        "Handling TicketItemStatusChangedEvent for restaurant: {}. Item: {}, New status: {}",
        event.getRestaurantId(),
        event.getItemId(),
        event.getNewStatus());
    broadcast(
        event.getRestaurantId(),
        "TICKET_ITEM_STATUS_CHANGED",
        java.util.Map.of("itemId", event.getItemId().toString(), "status", event.getNewStatus()));
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
            event.getAccountId(),
            event.getRestaurantId(),
            event.getTableCod(),
            count,
            call != null ? call.getId() : null);

    // Notify via SSE
    broadcast(event.getRestaurantId(), "WAITER_CALL", eventWithData);

    // Notify via Push
    try {
      broadcaster.callWaiter(event.getRestaurantId(), event.getTableCod());
    } catch (Exception e) {
      // Silently fail
    }
  }

  @EventListener
  public void handleWaiterCallDismissed(WaiterCallDismissedEvent event) {
    long count = listActiveWaiterCalls.execute(event.getRestaurantId()).size();
    WaiterCallDismissedEvent eventWithCount =
        new WaiterCallDismissedEvent(
            event.getAccountId(), event.getRestaurantId(), event.getCallId(), count);

    broadcast(event.getRestaurantId(), "WAITER_CALL_DISMISSED", eventWithCount);
  }

  @EventListener
  public void handleTableStatusChanged(TableStatusChangedEvent event) {
    logger.debug("Handling TableStatusChangedEvent for restaurant: {}", event.getRestaurantId());
    broadcast(
        event.getRestaurantId(),
        "TABLE_STATUS_CHANGED",
        java.util.Map.of("tableId", event.getTableId().toString()));
  }

  @EventListener
  public void handleTableTransferred(TableTransferredEvent event) {
    logger.debug("Handling TableTransferredEvent for restaurant: {}", event.getRestaurantId());
    broadcast(
        event.getRestaurantId(),
        "TABLE_TRANSFERRED",
        java.util.Map.of(
            "orderId", event.getOrderId().toString(),
            "sourceTableId", event.getSourceTableId().toString(),
            "targetTableId", event.getTargetTableId().toString(),
            "sourceTableCod", event.getSourceTableCod(),
            "targetTableCod", event.getTargetTableCod()));
  }

  private void broadcast(java.util.UUID restaurantId, String type, Object data) {
    broadcast(restaurantId, type, data, null);
  }

  private void broadcast(java.util.UUID restaurantId, String type, Object data, UUID initiatedBy) {
    sseService.broadcast(
        restaurantId,
        java.util.Map.of(
            "type", type, "data", data, "initiatedBy", initiatedBy != null ? initiatedBy : ""));
  }

  private TicketModel mapToModel(
      java.util.UUID restaurantId,
      Ticket ticket,
      dev.thiagooliveira.tablesplit.domain.order.Order order,
      String tableCod,
      Language language) {
    List<TicketItemModel> itemModels =
        ticket.getItems().stream()
            .map(
                item ->
                    TicketItemModel.fromDomain(
                        item,
                        order.getCustomerName(item.getCustomerId()),
                        ticket.getNote(),
                        ticket.getCreatedAt(),
                        language))
            .toList();

    String customerName = itemModels.isEmpty() ? "Cliente" : itemModels.get(0).getCustomerName();
    if (customerName == null || customerName.isBlank()) customerName = "Mesa " + tableCod;

    String timeAgo =
        dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.timeAgo(
            ticket.getCreatedAt(), messageSource, language);
    long minutesAgo = Duration.between(ticket.getCreatedAt(), Time.now()).toMinutes();
    boolean urgent = minutesAgo > 15 && ticket.getStatus() == TicketStatus.PENDING;

    return new TicketModel(
        restaurantId,
        ticket.getId(),
        tableCod,
        customerName,
        ticket.getStatus(),
        ticket.getCreatedAt(),
        timeAgo,
        itemModels,
        ticket.calculateTotal(),
        urgent,
        ticket.getNote(),
        order.getId());
  }
}

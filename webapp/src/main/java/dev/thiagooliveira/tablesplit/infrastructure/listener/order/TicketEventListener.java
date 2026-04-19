package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.application.notification.Broadcaster;
import dev.thiagooliveira.tablesplit.application.notification.RegisterWaiterCall;
import dev.thiagooliveira.tablesplit.application.notification.SseService;
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

  public TicketEventListener(
      SseService sseService,
      Broadcaster broadcaster,
      RegisterWaiterCall registerWaiterCall,
      dev.thiagooliveira.tablesplit.application.notification.ListActiveWaiterCalls
          listActiveWaiterCalls) {
    this.sseService = sseService;
    this.broadcaster = broadcaster;
    this.registerWaiterCall = registerWaiterCall;
    this.listActiveWaiterCalls = listActiveWaiterCalls;
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTicketCreated(TicketCreatedEvent event) {
    logger.debug("Handling TicketCreatedEvent for restaurant: {}", event.getRestaurantId());
    TicketModel model =
        mapToModel(
            event.getRestaurantId(), event.getTicket(), event.getOrder(), event.getTableCod());
    broadcast(event.getRestaurantId(), "TICKET_CREATED", model);

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
            event.getRestaurantId(),
            event.getTableCod(),
            count,
            call != null ? call.getId() : null);

    // Notify via SSE
    broadcast(event.getRestaurantId(), "WAITER_CALL", eventWithData);
  }

  @org.springframework.context.event.EventListener
  public void handleWaiterCallDismissed(
      dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent event) {
    long count = listActiveWaiterCalls.execute(event.getRestaurantId()).size();
    dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent eventWithCount =
        new dev.thiagooliveira.tablesplit.domain.event.WaiterCallDismissedEvent(
            event.getRestaurantId(), event.getCallId(), count);

    broadcast(event.getRestaurantId(), "WAITER_CALL_DISMISSED", eventWithCount);
  }

  @org.springframework.transaction.event.TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void handleTableStatusChanged(TableStatusChangedEvent event) {
    logger.debug("Handling TableStatusChangedEvent for restaurant: {}", event.getRestaurantId());
    broadcast(
        event.getRestaurantId(),
        "TABLE_STATUS_CHANGED",
        java.util.Map.of("tableId", event.getTableId().toString()));
  }

  private void broadcast(java.util.UUID restaurantId, String type, Object data) {
    sseService.broadcast(restaurantId, java.util.Map.of("type", type, "data", data));
  }

  private TicketModel mapToModel(
      java.util.UUID restaurantId,
      Ticket ticket,
      dev.thiagooliveira.tablesplit.domain.order.Order order,
      String tableCod) {
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
        ticket.getNote());
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

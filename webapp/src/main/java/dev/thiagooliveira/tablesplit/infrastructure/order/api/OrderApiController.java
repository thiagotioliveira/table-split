package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import dev.thiagooliveira.tablesplit.application.order.CancelTicketItem;
import dev.thiagooliveira.tablesplit.application.order.GetHistoryTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.GetTicket;
import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.application.order.MoveTicket;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.UpdateTicketItemStatus;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.OrdersApi;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.CancelItemRequest;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.MoveTicketRequest;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketsResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.UpdateItemStatusRequest;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketModel;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager")
public class OrderApiController implements OrdersApi {

  private final GetTickets getTickets;
  private final GetHistoryTickets getHistoryTickets;
  private final GetTicket getTicket;
  private final MoveTicket moveTicket;
  private final CancelTicketItem cancelTicketItem;
  private final PlaceOrder placeOrder;
  private final GetTables getTables;
  private final UpdateTicketItemStatus updateTicketItemStatus;
  private final TransactionalContext transactionalContext;
  private final OrderApiMapper mapper;

  public OrderApiController(
      GetTickets getTickets,
      GetHistoryTickets getHistoryTickets,
      GetTicket getTicket,
      MoveTicket moveTicket,
      CancelTicketItem cancelTicketItem,
      PlaceOrder placeOrder,
      GetTables getTables,
      UpdateTicketItemStatus updateTicketItemStatus,
      TransactionalContext transactionalContext,
      OrderApiMapper mapper) {
    this.getTickets = getTickets;
    this.getHistoryTickets = getHistoryTickets;
    this.getTicket = getTicket;
    this.moveTicket = moveTicket;
    this.cancelTicketItem = cancelTicketItem;
    this.placeOrder = placeOrder;
    this.getTables = getTables;
    this.updateTicketItemStatus = updateTicketItemStatus;
    this.transactionalContext = transactionalContext;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<Void> cancelTicketItem(CancelItemRequest cancelItemRequest) {
    transactionalContext.execute(
        () ->
            cancelTicketItem.execute(
                cancelItemRequest.getItemId(),
                cancelItemRequest.getQuantity(),
                cancelItemRequest.getReason()));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<
          dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.HistoryResponse>
      getOrderHistory(OffsetDateTime start, OffsetDateTime end) {
    AccountContext context = getContext();

    ZonedDateTime zStart =
        start != null ? start.toZonedDateTime().withZoneSameInstant(Time.getZoneId()) : null;
    ZonedDateTime zEnd =
        end != null ? end.toZonedDateTime().withZoneSameInstant(Time.getZoneId()) : null;

    List<GetTickets.TicketWithTable> history =
        getHistoryTickets.execute(context.getRestaurant().getId(), zStart, zEnd);

    List<TicketModel> orders =
        history.stream()
            .map(
                tw ->
                    mapper.mapToModel(
                        tw.ticket(), tw.order(), tw.tableCod(), context.getUser().getLanguage()))
            .toList();

    BigDecimal totalRevenue =
        orders.stream().map(TicketModel::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal avgTicket =
        orders.isEmpty()
            ? BigDecimal.ZERO
            : totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);

    dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.HistoryResponse response =
        new dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.HistoryResponse()
            .orders(orders.stream().map(mapper::mapToTicketResponse).toList())
            .totalOrders(orders.size())
            .totalRevenue(totalRevenue.doubleValue())
            .avgTicket(avgTicket.doubleValue());

    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Long> getPendingCount() {
    AccountContext context = getContext();
    return ResponseEntity.ok(getTickets.countPending(context.getRestaurant().getId()));
  }

  @Override
  public ResponseEntity<TicketResponse> getTicketById(UUID id) {
    AccountContext context = getContext();
    return getTicket
        .execute(id)
        .map(
            tw ->
                mapper.mapToModel(
                    tw.ticket(), tw.order(), tw.tableCod(), context.getUser().getLanguage()))
        .map(mapper::mapToTicketResponse)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
  }

  @Override
  public ResponseEntity<TicketsResponse> getTickets(OffsetDateTime start) {
    AccountContext context = getContext();

    ZonedDateTime zStart =
        start != null
            ? start.toZonedDateTime().withZoneSameInstant(Time.getZoneId())
            : ZonedDateTime.now(Time.getZoneId()).toLocalDate().atStartOfDay(Time.getZoneId());

    List<GetTickets.TicketWithTable> ticketsWithTables =
        getTickets.execute(context.getRestaurant().getId(), zStart);

    List<TicketModel> allTickets =
        ticketsWithTables.stream()
            .map(
                tw ->
                    mapper.mapToModel(
                        tw.ticket(), tw.order(), tw.tableCod(), context.getUser().getLanguage()))
            .toList();

    Map<String, List<TicketModel>> ticketsByStatus =
        allTickets.stream().collect(Collectors.groupingBy(t -> t.getStatus().name()));

    if (ticketsByStatus.containsKey(TicketStatus.DELIVERED.name())) {
      List<TicketModel> delivered =
          new java.util.ArrayList<>(ticketsByStatus.get(TicketStatus.DELIVERED.name()));
      delivered.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

      if (delivered.size() > 10) {
        ticketsByStatus.put(TicketStatus.DELIVERED.name(), delivered.subList(0, 10));
      } else {
        ticketsByStatus.put(TicketStatus.DELIVERED.name(), delivered);
      }
    }

    ZonedDateTime endOfDay = zStart.plusDays(1).minusNanos(1);
    long deliveredTodayCount =
        getHistoryTickets.execute(context.getRestaurant().getId(), zStart, endOfDay).stream()
            .filter(t -> t.ticket().getStatus().isDelivered())
            .count();

    TicketsResponse response =
        new TicketsResponse()
            .ticketsByStatus(
                ticketsByStatus.entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream().map(mapper::mapToTicketResponse).toList())))
            .allTickets(allTickets.stream().map(mapper::mapToTicketResponse).toList())
            .pendingCount(
                ticketsByStatus.getOrDefault(TicketStatus.PENDING.name(), List.of()).size())
            .preparingCount(
                ticketsByStatus.getOrDefault(TicketStatus.PREPARING.name(), List.of()).size())
            .readyCount(ticketsByStatus.getOrDefault(TicketStatus.READY.name(), List.of()).size())
            .deliveredCount(
                ticketsByStatus.getOrDefault(TicketStatus.DELIVERED.name(), List.of()).size())
            .deliveredTodayCount((int) deliveredTodayCount)
            .totalCount(allTickets.size());

    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> moveTicket(MoveTicketRequest moveTicketRequest) {
    transactionalContext.execute(
        () ->
            moveTicket.execute(
                moveTicketRequest.getTicketId(),
                TicketStatus.valueOf(moveTicketRequest.getStatus())));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> placeOrder(UUID tableId, PlaceOrderRequest request) {
    AccountContext account = getContext();

    var table =
        getTables
            .findById(tableId)
            .orElseThrow(() -> new NotFoundException("error.table.not.found"));

    var command =
        mapper.mapToCommand(
            table.getRestaurantId(),
            table.getCod(),
            account.getRestaurant().getServiceFee(),
            request);

    transactionalContext.execute(() -> placeOrder.execute(command));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> updateItemStatus(UUID itemId, UpdateItemStatusRequest request) {
    transactionalContext.execute(
        () ->
            updateTicketItemStatus.execute(
                itemId,
                dev.thiagooliveira.tablesplit.domain.order.TicketStatus.valueOf(
                    request.getStatus().name())));
    return ResponseEntity.noContent().build();
  }

  protected AccountContext getContext() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (AccountContext) auth.getPrincipal();
  }
}

package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.CancelTicketItem;
import dev.thiagooliveira.tablesplit.application.order.GetHistoryTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTicket;
import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTickets.TicketWithTable;
import dev.thiagooliveira.tablesplit.application.order.MoveTicket;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.HistoryResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/orders")
@ManagerModule(Module.ORDERS)
public class OrderController {

  private final GetTickets getTickets;
  private final GetHistoryTickets getHistoryTickets;
  private final GetTicket getTicket;
  private final MoveTicket moveTicket;
  private final CancelTicketItem cancelTicketItem;
  private final TransactionalContext transactionalContext;
  private final MessageSource messageSource;

  public OrderController(
      GetTickets getTickets,
      GetHistoryTickets getHistoryTickets,
      GetTicket getTicket,
      MoveTicket moveTicket,
      CancelTicketItem cancelTicketItem,
      TransactionalContext transactionalContext,
      MessageSource messageSource) {
    this.getTickets = getTickets;
    this.getHistoryTickets = getHistoryTickets;
    this.getTicket = getTicket;
    this.moveTicket = moveTicket;
    this.cancelTicketItem = cancelTicketItem;
    this.transactionalContext = transactionalContext;
    this.messageSource = messageSource;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    populateModel(auth, model);
    return "orders";
  }

  private void populateModel(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    List<TicketWithTable> ticketsWithTables = getTickets.execute(context.getRestaurant().getId());

    List<TicketModel> allTickets =
        ticketsWithTables.stream()
            .map(
                tw ->
                    mapToModel(
                        tw.ticket(), tw.order(), tw.tableCod(), context.getUser().getLanguage()))
            .toList();

    Map<String, List<TicketModel>> ticketsByStatus =
        allTickets.stream().collect(Collectors.groupingBy(t -> t.getStatus().name()));

    model.addAttribute("ticketsByStatus", ticketsByStatus);
    model.addAttribute("allTickets", allTickets);

    // Add counts for badges
    model.addAttribute("pendingCount", ticketsByStatus.getOrDefault("PENDING", List.of()).size());
    model.addAttribute(
        "preparingCount", ticketsByStatus.getOrDefault("PREPARING", List.of()).size());
    model.addAttribute(
        "deliveredCount", ticketsByStatus.getOrDefault("DELIVERED", List.of()).size());
    model.addAttribute("readyCount", ticketsByStatus.getOrDefault("READY", List.of()).size());

    ZonedDateTime startOfDay =
        ZonedDateTime.now(Time.getZoneId()).toLocalDate().atStartOfDay(Time.getZoneId());
    ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
    long deliveredTodayCount =
        getHistoryTickets.execute(context.getRestaurant().getId(), startOfDay, endOfDay).stream()
            .filter(
                t ->
                    t.ticket().getStatus()
                        == dev.thiagooliveira.tablesplit.domain.order.TicketStatus.DELIVERED)
            .count();

    model.addAttribute("deliveredTodayCount", deliveredTodayCount);
    model.addAttribute("totalCount", allTickets.size());
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    model.addAttribute("currencyCode", context.getRestaurant().getCurrency().name());
    model.addAttribute("zoneId", Time.getZoneId().getId());
  }

  @GetMapping("/history")
  @ResponseBody
  public HistoryResponse history(
      Authentication auth,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          java.time.LocalDateTime start,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          java.time.LocalDateTime end) {
    AccountContext context = (AccountContext) auth.getPrincipal();

    ZonedDateTime zStart = start != null ? start.atZone(Time.getZoneId()) : null;
    ZonedDateTime zEnd = end != null ? end.atZone(Time.getZoneId()) : null;

    List<TicketWithTable> history =
        getHistoryTickets.execute(context.getRestaurant().getId(), zStart, zEnd);

    List<TicketModel> orders =
        history.stream()
            .map(
                tw ->
                    mapToModel(
                        tw.ticket(), tw.order(), tw.tableCod(), context.getUser().getLanguage()))
            .toList();

    BigDecimal totalRevenue =
        orders.stream().map(TicketModel::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal avgTicket =
        orders.isEmpty()
            ? BigDecimal.ZERO
            : totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);

    return new HistoryResponse(orders, orders.size(), totalRevenue, avgTicket);
  }

  @PostMapping("/move")
  public String move(Authentication auth, @RequestBody MoveTicketRequest request, Model model) {
    transactionalContext.execute(
        () -> moveTicket.execute(request.ticketId(), TicketStatus.valueOf(request.status())));

    populateModel(auth, model);
    return "orders :: dashboardFragment";
  }

  @GetMapping("/{id}")
  @ResponseBody
  public TicketModel getTicket(@PathVariable UUID id) {
    return getTicket
        .execute(id)
        .map(
            tw ->
                mapToModel(
                    tw.ticket(),
                    tw.order(),
                    tw.tableCod(),
                    ((AccountContext)
                            org.springframework.security.core.context.SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal())
                        .getUser()
                        .getLanguage()))
        .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
  }

  @GetMapping("/count/pending")
  @ResponseBody
  public long getPendingCount(Authentication auth) {
    var context = (AccountContext) auth.getPrincipal();
    return getTickets.countPending(context.getRestaurant().getId());
  }

  @PostMapping("/item/cancel")
  @ResponseBody
  public void cancelItem(@RequestBody CancelItemRequest request) {
    transactionalContext.execute(
        () -> cancelTicketItem.execute(request.itemId(), request.quantity(), request.reason()));
  }

  public record MoveTicketRequest(UUID ticketId, String status) {}

  public record CancelItemRequest(UUID itemId, int quantity, String reason) {}

  private TicketModel mapToModel(
      Ticket ticket,
      dev.thiagooliveira.tablesplit.domain.order.Order order,
      String tableCod,
      Language userLanguage) {
    List<TicketItemModel> itemModels =
        ticket.getItems().stream()
            .map(
                item ->
                    TicketItemModel.fromDomain(
                        item,
                        order.getCustomerName(item.getCustomerId()),
                        ticket.getCreatedAt(),
                        userLanguage))
            .toList();

    String customerName = itemModels.isEmpty() ? "Cliente" : itemModels.get(0).getCustomerName();
    if (customerName == null || customerName.isBlank()) customerName = "Mesa " + tableCod;

    String timeAgo =
        dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.timeAgo(
            ticket.getCreatedAt(), messageSource, userLanguage);
    long minutes = Duration.between(ticket.getCreatedAt(), Time.nowZonedDateTime()).toMinutes();
    boolean urgent = minutes > 15 && ticket.getStatus().isPending();

    return new TicketModel(
        order.getRestaurantId(),
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
}

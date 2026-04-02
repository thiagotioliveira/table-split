package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.CancelTicketItem;
import dev.thiagooliveira.tablesplit.application.order.GetTicket;
import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTickets.TicketWithTable;
import dev.thiagooliveira.tablesplit.application.order.MoveTicket;
import dev.thiagooliveira.tablesplit.application.order.UpdateTicketItemStatus;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketModel;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/orders")
@ManagerModule(Module.ORDERS)
public class OrderController {

  private final GetTickets getTickets;
  private final GetTicket getTicket;
  private final MoveTicket moveTicket;
  private final UpdateTicketItemStatus updateTicketItemStatus;
  private final CancelTicketItem cancelTicketItem;
  private final TransactionalContext transactionalContext;

  public OrderController(
      GetTickets getTickets,
      GetTicket getTicket,
      MoveTicket moveTicket,
      UpdateTicketItemStatus updateTicketItemStatus,
      CancelTicketItem cancelTicketItem,
      TransactionalContext transactionalContext) {
    this.getTickets = getTickets;
    this.getTicket = getTicket;
    this.moveTicket = moveTicket;
    this.updateTicketItemStatus = updateTicketItemStatus;
    this.cancelTicketItem = cancelTicketItem;
    this.transactionalContext = transactionalContext;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    List<TicketWithTable> ticketsWithTables = getTickets.execute(context.getRestaurant().getId());

    List<TicketModel> allTickets =
        ticketsWithTables.stream()
            .map(tw -> mapToModel(tw.ticket(), tw.order(), tw.tableCod()))
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
    model.addAttribute("totalCount", allTickets.size());
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());

    return "orders";
  }

  @PostMapping("/move")
  @ResponseBody
  public void move(@RequestBody MoveTicketRequest request) {
    transactionalContext.execute(
        () -> moveTicket.execute(request.ticketId(), TicketStatus.valueOf(request.status())));
  }

  @GetMapping("/{id}")
  @ResponseBody
  public TicketModel getTicket(@PathVariable UUID id) {
    return getTicket
        .execute(id)
        .map(tw -> mapToModel(tw.ticket(), tw.order(), tw.tableCod()))
        .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
  }

  @PostMapping("/item/status")
  @ResponseBody
  public void updateItemStatus(@RequestBody UpdateItemStatusRequest request) {
    transactionalContext.execute(
        () ->
            updateTicketItemStatus.execute(
                request.itemId(), TicketStatus.valueOf(request.status())));
  }

  @PostMapping("/item/cancel")
  @ResponseBody
  public void cancelItem(@RequestBody CancelItemRequest request) {
    transactionalContext.execute(
        () -> cancelTicketItem.execute(request.itemId(), request.quantity(), request.reason()));
  }

  public record MoveTicketRequest(UUID ticketId, String status) {}

  public record UpdateItemStatusRequest(UUID itemId, String status) {}

  public record CancelItemRequest(UUID itemId, int quantity, String reason) {}

  private TicketModel mapToModel(
      Ticket ticket, dev.thiagooliveira.tablesplit.domain.order.Order order, String tableCod) {
    List<TicketItemModel> itemModels =
        ticket.getItems().stream()
            .map(
                item ->
                    new TicketItemModel(
                        item.getId(),
                        item.getCustomerId(),
                        order.getCustomerName(item.getCustomerId()),
                        item.getName()
                            .getOrDefault(Language.PT, item.getName().values().iterator().next()),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getNote(),
                        item.getStatus().name(),
                        item.getStatus().getCssClass(),
                        ticket.getCreatedAt()))
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

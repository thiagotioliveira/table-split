package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTickets.TicketWithTable;
import dev.thiagooliveira.tablesplit.application.order.MoveTicket;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/orders")
@ManagerModule(Module.ORDERS)
public class OrderController {

  private final GetTickets getTickets;
  private final MoveTicket moveTicket;

  public OrderController(GetTickets getTickets, MoveTicket moveTicket) {
    this.getTickets = getTickets;
    this.moveTicket = moveTicket;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    List<TicketWithTable> ticketsWithTables = getTickets.execute(context.getRestaurant().getId());

    List<TicketModel> allTickets =
        ticketsWithTables.stream().map(tw -> mapToModel(tw.ticket(), tw.tableCod())).toList();

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
    moveTicket.execute(request.ticketId(), TicketStatus.valueOf(request.status()));
  }

  public record MoveTicketRequest(UUID ticketId, String status) {}

  private TicketModel mapToModel(Ticket ticket, String tableCod) {
    List<TicketItemModel> itemModels =
        ticket.getItems().stream()
            .map(
                item ->
                    new TicketItemModel(
                        item.getCustomerName(),
                        item.getName()
                            .getOrDefault(Language.PT, item.getName().values().iterator().next()),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getNote(),
                        item.getStatus().getLabel(),
                        item.getStatus().getCssClass()))
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

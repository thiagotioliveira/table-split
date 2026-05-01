package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.GetHistoryTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.application.order.GetTickets.TicketWithTable;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketModel;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
@ManagerModule(Module.ORDERS)
public class OrderController {

  private final GetTickets getTickets;
  private final GetHistoryTickets getHistoryTickets;
  private final OrderApiMapper mapper;

  public OrderController(
      GetTickets getTickets, GetHistoryTickets getHistoryTickets, OrderApiMapper mapper) {
    this.getTickets = getTickets;
    this.getHistoryTickets = getHistoryTickets;
    this.mapper = mapper;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    populateModel(auth, model);
    return "orders";
  }

  private void populateModel(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();

    ZonedDateTime startOfDay =
        ZonedDateTime.now(Time.getZoneId()).toLocalDate().atStartOfDay(Time.getZoneId());

    List<TicketWithTable> ticketsWithTables =
        getTickets.execute(context.getRestaurant().getId(), startOfDay);

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

    model.addAttribute("ticketsByStatus", ticketsByStatus);
    model.addAttribute("allTickets", allTickets);

    model.addAttribute(
        "pendingCount",
        ticketsByStatus.getOrDefault(TicketStatus.PENDING.name(), List.of()).size());
    model.addAttribute(
        "preparingCount",
        ticketsByStatus.getOrDefault(TicketStatus.PREPARING.name(), List.of()).size());
    model.addAttribute(
        "readyCount", ticketsByStatus.getOrDefault(TicketStatus.READY.name(), List.of()).size());
    model.addAttribute(
        "deliveredCount",
        ticketsByStatus.getOrDefault(TicketStatus.DELIVERED.name(), List.of()).size());

    ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
    long deliveredTodayCount =
        getHistoryTickets.execute(context.getRestaurant().getId(), startOfDay, endOfDay).stream()
            .filter(t -> t.ticket().getStatus().isDelivered())
            .count();

    model.addAttribute("deliveredTodayCount", deliveredTodayCount);
    model.addAttribute("totalCount", allTickets.size());
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("currency", context.getRestaurant().getCurrency());
    model.addAttribute("zoneId", Time.getZoneId().getId());
    model.addAttribute("userLanguage", context.getUser().getLanguage());
    model.addAttribute("serviceFee", context.getRestaurant().getServiceFee());

    model.addAttribute("STATUS_PENDING", TicketStatus.PENDING.name());
    model.addAttribute("STATUS_PREPARING", TicketStatus.PREPARING.name());
    model.addAttribute("STATUS_READY", TicketStatus.READY.name());
    model.addAttribute("STATUS_DELIVERED", TicketStatus.DELIVERED.name());
    model.addAttribute("STATUS_CANCELLED", TicketStatus.CANCELLED.name());
  }
}

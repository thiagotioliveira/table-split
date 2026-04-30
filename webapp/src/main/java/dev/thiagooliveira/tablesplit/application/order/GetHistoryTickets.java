package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.order.GetTickets.TicketWithTable;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GetHistoryTickets {

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;

  public GetHistoryTickets(OrderRepository orderRepository, TableRepository tableRepository) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
  }

  public List<TicketWithTable> execute(UUID restaurantId, ZonedDateTime start, ZonedDateTime end) {
    // For now, we fetch all orders and filter. In a real scenario, this should be optimized.
    List<Order> orders =
        new ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN));
    orders.addAll(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.CLOSED));
    orders.addAll(
        orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.CANCELLED));

    List<TicketWithTable> results = new ArrayList<>();
    for (Order order : orders) {
      String tableCod =
          tableRepository.findById(order.getTableId()).map(Table::getCod).orElse("??");
      for (Ticket ticket : order.getTickets()) {
        if (ticket.getStatus() == TicketStatus.DELIVERED
            || ticket.getStatus() == TicketStatus.CANCELLED) {
          if (start != null && ticket.getCreatedAt().isBefore(start)) continue;
          if (end != null && ticket.getCreatedAt().isAfter(end)) continue;
          results.add(new TicketWithTable(ticket, order, tableCod));
        }
      }
    }
    // Sort by createdAt desc
    results.sort((a, b) -> b.ticket().getCreatedAt().compareTo(a.ticket().getCreatedAt()));
    return results;
  }
}

package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GetTickets {

  public record TicketWithTable(Ticket ticket, Order order, String tableCod) {}

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;

  public GetTickets(OrderRepository orderRepository, TableRepository tableRepository) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
  }

  public List<TicketWithTable> execute(
      UUID restaurantId, java.time.ZonedDateTime closedAtThreshold) {
    List<Order> orders =
        new java.util.ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN));

    if (closedAtThreshold != null) {
      orders.addAll(
          orderRepository.findAllByRestaurantIdAndStatusAndClosedAtAfter(
              restaurantId, OrderStatus.CLOSED, closedAtThreshold));
    } else {
      orders.addAll(
          orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.CLOSED).stream()
              .filter(Order::hasWaitingTickets)
              .toList());
    }

    List<TicketWithTable> results = new ArrayList<>();
    for (Order order : orders) {
      String tableCod =
          tableRepository.findById(order.getTableId()).map(Table::getCod).orElse("??");
      for (Ticket ticket : order.getTickets()) {
        results.add(new TicketWithTable(ticket, order, tableCod));
      }
    }
    return results;
  }

  public long countPending(UUID restaurantId) {
    List<Order> orders =
        new java.util.ArrayList<>(
            orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN));
    orders.addAll(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.CLOSED));

    return orders.stream().flatMap(o -> o.getTickets().stream()).filter(Ticket::isPending).count();
  }
}

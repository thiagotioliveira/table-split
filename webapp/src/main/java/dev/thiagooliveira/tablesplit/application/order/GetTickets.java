package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
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

  public List<TicketWithTable> execute(UUID restaurantId) {
    List<Order> openOrders =
        orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN);
    List<TicketWithTable> results = new ArrayList<>();
    for (Order order : openOrders) {
      String tableCod =
          tableRepository.findById(order.getTableId()).map(Table::getCod).orElse("??");
      for (Ticket ticket : order.getTickets()) {
        results.add(new TicketWithTable(ticket, order, tableCod));
      }
    }
    return results;
  }
}

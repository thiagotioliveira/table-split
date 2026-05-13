package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.common.Pagination;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.TicketWithTable;
import java.time.ZonedDateTime;
import java.util.UUID;

public class GetHistoryTickets {

  private final OrderRepository orderRepository;

  public GetHistoryTickets(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Pagination<TicketWithTable> execute(
      UUID restaurantId, ZonedDateTime start, ZonedDateTime end, int page, int size) {
    return orderRepository.findHistoryTickets(restaurantId, start, end, page, size);
  }

  public OrderRepository.HistorySummary getSummary(
      UUID restaurantId, ZonedDateTime start, ZonedDateTime end) {
    return orderRepository.getHistorySummary(restaurantId, start, end);
  }
}

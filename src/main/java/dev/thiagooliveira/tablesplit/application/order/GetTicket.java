package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import java.util.Optional;
import java.util.UUID;

public class GetTicket {

  public record TicketWithTable(Ticket ticket, String tableCod) {}

  private final OrderRepository orderRepository;
  private final TableRepository tableRepository;

  public GetTicket(OrderRepository orderRepository, TableRepository tableRepository) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
  }

  public Optional<TicketWithTable> execute(UUID ticketId) {
    return orderRepository
        .findByTicketId(ticketId)
        .flatMap(
            order -> {
              String tableCod =
                  tableRepository.findById(order.getTableId()).map(Table::getCod).orElse("??");
              return order.getTickets().stream()
                  .filter(t -> t.getId().equals(ticketId))
                  .findFirst()
                  .map(ticket -> new TicketWithTable(ticket, tableCod));
            });
  }
}

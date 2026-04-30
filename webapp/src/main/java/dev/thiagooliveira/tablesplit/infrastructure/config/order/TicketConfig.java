package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketConfig {

  @Bean
  public GetTicket getTicket(OrderRepository orderRepository, TableRepository tableRepository) {
    return new GetTicket(orderRepository, tableRepository);
  }

  @Bean
  public UpdateTicketItemStatus updateTicketItemStatus(
      OrderRepository orderRepository, SyncTableStatus syncTableStatus) {
    return new UpdateTicketItemStatus(orderRepository, syncTableStatus);
  }

  @Bean
  public GetTickets getTickets(OrderRepository orderRepository, TableRepository tableRepository) {
    return new GetTickets(orderRepository, tableRepository);
  }

  @Bean
  public GetHistoryTickets getHistoryTickets(
      OrderRepository orderRepository, TableRepository tableRepository) {
    return new GetHistoryTickets(orderRepository, tableRepository);
  }

  @Bean
  public MoveTicket moveTicket(OrderRepository orderRepository, SyncTableStatus syncTableStatus) {
    return new MoveTicket(orderRepository, syncTableStatus);
  }

  @Bean
  public CancelTicketItem cancelTicketItem(OrderRepository orderRepository) {
    return new CancelTicketItem(orderRepository);
  }

  @Bean
  public CallWaiter callWaiter(TableRepository tableRepository) {
    return new CallWaiter(tableRepository);
  }
}

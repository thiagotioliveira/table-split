package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.order.*;
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
      OrderRepository orderRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus) {
    return new UpdateTicketItemStatus(orderRepository, eventPublisher, syncTableStatus);
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
  public MoveTicket moveTicket(
      OrderRepository orderRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus) {
    return new MoveTicket(orderRepository, eventPublisher, syncTableStatus);
  }

  @Bean
  public CancelTicketItem cancelTicketItem(
      OrderRepository orderRepository, EventPublisher eventPublisher) {
    return new CancelTicketItem(orderRepository, eventPublisher);
  }

  @Bean
  public CallWaiter callWaiter(TableRepository tableRepository, EventPublisher eventPublisher) {
    return new CallWaiter(tableRepository, eventPublisher);
  }
}

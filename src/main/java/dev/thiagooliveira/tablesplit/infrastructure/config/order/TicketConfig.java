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
      OrderRepository orderRepository, EventPublisher eventPublisher) {
    return new UpdateTicketItemStatus(orderRepository, eventPublisher);
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
      TableRepository tableRepository,
      EventPublisher eventPublisher) {
    return new MoveTicket(orderRepository, tableRepository, eventPublisher);
  }

  @Bean
  public CancelTicketItem cancelTicketItem(
      OrderRepository orderRepository, EventPublisher eventPublisher) {
    return new CancelTicketItem(orderRepository, eventPublisher);
  }
}

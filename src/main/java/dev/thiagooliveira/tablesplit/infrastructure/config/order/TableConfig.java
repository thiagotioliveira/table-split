package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.order.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableConfig {
  @Bean
  public GetTables getTables(TableRepository tableRepository) {
    return new GetTables(tableRepository);
  }

  @Bean
  public CreateTable createTable(TableRepository tableRepository, EventPublisher eventPublisher) {
    return new CreateTable(tableRepository, eventPublisher);
  }

  @Bean
  public OpenTable openTable(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      EventPublisher eventPublisher) {
    return new OpenTable(tableRepository, orderRepository, eventPublisher);
  }

  @Bean
  public CloseTable closeTable(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      EventPublisher eventPublisher) {
    return new CloseTable(tableRepository, orderRepository, eventPublisher);
  }
}

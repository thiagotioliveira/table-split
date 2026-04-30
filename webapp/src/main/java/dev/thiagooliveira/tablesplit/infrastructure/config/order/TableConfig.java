package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableConfig {
  @Bean
  public GetTables getTables(TableRepository tableRepository) {
    return new GetTables(tableRepository);
  }

  @Bean
  public CreateTable createTable(
      TableRepository tableRepository, PlanLimitValidator planLimitValidator) {
    return new CreateTable(tableRepository, planLimitValidator);
  }

  @Bean
  public OpenTable openTable(
      TableRepository tableRepository,
      OrderRepository orderRepository,
      SyncTableStatus syncTableStatus) {
    return new OpenTable(tableRepository, orderRepository, syncTableStatus);
  }

  @Bean
  public CloseTable closeTable(TableRepository tableRepository, OrderRepository orderRepository) {
    return new CloseTable(tableRepository, orderRepository);
  }

  @Bean
  public SyncTableStatus syncTableStatus(TableRepository tableRepository) {
    return new SyncTableStatus(tableRepository);
  }
}

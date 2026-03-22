package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.CloseTable;
import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.application.order.MoveTicket;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.order.OrderRepository;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.TableRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

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

  @Bean
  public PlaceOrder placeOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      EventPublisher eventPublisher) {
    return new PlaceOrder(
        openTable, tableRepository, orderRepository, itemRepository, eventPublisher);
  }

  @Bean
  public GetTables getTables(TableRepository tableRepository) {
    return new GetTables(tableRepository);
  }

  @Bean
  public CreateTable createTable(TableRepository tableRepository, EventPublisher eventPublisher) {
    return new CreateTable(tableRepository, eventPublisher);
  }

  @Bean
  public GetTickets getTickets(OrderRepository orderRepository, TableRepository tableRepository) {
    return new GetTickets(orderRepository, tableRepository);
  }

  @Bean
  public MoveTicket moveTicket(
      OrderRepository orderRepository,
      TableRepository tableRepository,
      EventPublisher eventPublisher) {
    return new MoveTicket(orderRepository, tableRepository, eventPublisher);
  }
}

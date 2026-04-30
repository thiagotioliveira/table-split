package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.*;
import dev.thiagooliveira.tablesplit.domain.order.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

  @Bean
  public OrderService orderService(OrderRepository orderRepository) {
    return new OrderService(orderRepository);
  }

  @Bean
  public PlaceOrder placeOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      SyncTableStatus syncTableStatus,
      OrderService orderService) {
    return new PlaceOrder(
        openTable, tableRepository, orderRepository, itemRepository, syncTableStatus, orderService);
  }

  @Bean
  public GetOrder getOrder(OrderRepository orderRepository) {
    return new GetOrder(orderRepository);
  }

  @Bean
  public UpdateCustomerName updateCustomerName(OrderRepository orderRepository) {
    return new UpdateCustomerName(orderRepository);
  }

  @Bean
  public DeleteTable deleteTable(TableRepository tableRepository) {
    return new DeleteTable(tableRepository);
  }
}

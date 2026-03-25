package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

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
  public GetOrder getOrder(OrderRepository orderRepository) {
    return new GetOrder(orderRepository);
  }
}

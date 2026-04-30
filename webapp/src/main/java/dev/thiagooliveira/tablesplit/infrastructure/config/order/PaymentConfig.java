package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.order.CloseTable;
import dev.thiagooliveira.tablesplit.application.order.DeletePayment;
import dev.thiagooliveira.tablesplit.application.order.ProcessPayment;
import dev.thiagooliveira.tablesplit.application.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

  @Bean
  public ProcessPayment processPayment(OrderRepository orderRepository, CloseTable closeTable) {
    return new ProcessPayment(orderRepository, closeTable);
  }

  @Bean
  public DeletePayment deletePayment(
      OrderRepository orderRepository, TableRepository tableRepository) {
    return new DeletePayment(orderRepository, tableRepository);
  }
}

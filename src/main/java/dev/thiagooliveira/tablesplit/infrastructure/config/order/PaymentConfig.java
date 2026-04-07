package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.order.DeletePayment;
import dev.thiagooliveira.tablesplit.application.order.OrderRepository;
import dev.thiagooliveira.tablesplit.application.order.ProcessPayment;
import dev.thiagooliveira.tablesplit.application.order.TableRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

  @Bean
  public ProcessPayment processPayment(
      OrderRepository orderRepository,
      TableRepository tableRepository,
      EventPublisher eventPublisher) {
    return new ProcessPayment(orderRepository, tableRepository, eventPublisher);
  }

  @Bean
  public DeletePayment deletePayment(
      OrderRepository orderRepository, TableRepository tableRepository) {
    return new DeletePayment(orderRepository, tableRepository);
  }
}

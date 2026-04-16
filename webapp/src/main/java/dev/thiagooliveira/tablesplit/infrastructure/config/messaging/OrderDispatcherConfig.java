package dev.thiagooliveira.tablesplit.infrastructure.config.messaging;

import dev.thiagooliveira.tablesplit.infrastructure.messaging.order.OrderDispatcher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderDispatcherConfig {

  @Bean
  @ConditionalOnBean(RabbitTemplate.class)
  public OrderDispatcher orderDispatcher(RabbitTemplate rabbitTemplate) {
    return new OrderDispatcher(rabbitTemplate);
  }
}

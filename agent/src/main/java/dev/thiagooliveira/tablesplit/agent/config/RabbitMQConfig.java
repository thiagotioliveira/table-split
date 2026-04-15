package dev.thiagooliveira.tablesplit.agent.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    // Isso é importante: como o DTO original não existe aqui,
    // o Jackson vai converter para um Map ou LinkedHashMap automaticamente.
    return converter;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(producerJackson2MessageConverter());
    return factory;
  }
}

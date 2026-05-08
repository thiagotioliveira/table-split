package dev.thiagooliveira.tablesplit.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.tablesplit.infrastructure.order.listener.TicketMessageMapper;
import dev.thiagooliveira.tablesplit.infrastructure.order.listener.TicketMessagingListener;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "app.integration.rabbit.enabled",
    havingValue = "true")
public class RabbitMQConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(RabbitMQConfig.class);

  @org.springframework.beans.factory.annotation.Value(
      "${app.integration.rabbit.order-ticket-exchange}")
  private String exchangeName;

  @Bean
  public TopicExchange ticketIntegrationExchange() {
    return new TopicExchange(exchangeName);
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @jakarta.annotation.PostConstruct
  public void init() {
    log.info(
        "RabbitMQConfig: Integration is ENABLED and configuration is being loaded. Exchange: {}",
        exchangeName);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter);
    return template;
  }

  @Bean
  public TicketMessagingListener orderDispatcher(
      RabbitTemplate rabbitTemplate, TicketMessageMapper ticketMessageMapper) {
    return new TicketMessagingListener(rabbitTemplate, exchangeName, ticketMessageMapper);
  }
}

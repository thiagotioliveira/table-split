package dev.thiagooliveira.tablesplit.infrastructure.config.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
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

  public static final String EXCHANGE_NAME = "order.integration.exchange";

  @Bean
  public TopicExchange orderIntegrationExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @jakarta.annotation.PostConstruct
  public void init() {
    log.info("RabbitMQConfig: Integration is ENABLED and configuration is being loaded.");
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter);
    return template;
  }

  @Bean
  public dev.thiagooliveira.tablesplit.infrastructure.messaging.order.OrderDispatcher
      orderDispatcher(RabbitTemplate rabbitTemplate) {
    return new dev.thiagooliveira.tablesplit.infrastructure.messaging.order.OrderDispatcher(
        rabbitTemplate);
  }
}

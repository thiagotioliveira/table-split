package dev.thiagooliveira.tablesplit.agent.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter(
      com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
    // This tells Jackson to ignore the __TypeId__ header from the webapp
    // and use the method parameter type (IntegrationOrderDTO) instead.
    converter.setTypePrecedence(
        org.springframework.amqp.support.converter.Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
    return converter;
  }
}

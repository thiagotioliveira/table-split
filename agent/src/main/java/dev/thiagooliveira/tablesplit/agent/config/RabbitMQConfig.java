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
    
    // Forçamos o conversor a ignorar o cabeçalho __TypeId__ enviado pelo WebApp
    // e usar apenas a classe que definimos no parâmetro do método (Inferred)
    org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper typeMapper = 
        new org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper();
    typeMapper.setTypePrecedence(org.springframework.amqp.support.converter.Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
    
    // Mapeamento explícito: De (Nome enviado pelo WebApp) -> Para (Classe local do Agente)
    java.util.Map<String, Class<?>> idClassMapping = new java.util.HashMap<>();
    idClassMapping.put("dev.thiagooliveira.tablesplit.infrastructure.messaging.order.model.IntegrationOrderDTO", 
                       dev.thiagooliveira.tablesplit.agent.model.IntegrationOrderDTO.class);
    typeMapper.setIdClassMapping(idClassMapping);
    
    typeMapper.addTrustedPackages("*"); 
    
    converter.setJavaTypeMapper(typeMapper);
    return converter;
  }
}

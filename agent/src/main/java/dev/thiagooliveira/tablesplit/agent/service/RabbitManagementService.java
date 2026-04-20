package dev.thiagooliveira.tablesplit.agent.service;

import dev.thiagooliveira.tablesplit.agent.config.AgentConfig;
import dev.thiagooliveira.tablesplit.agent.listener.OrderListener;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

@Service
public class RabbitManagementService {

  private static final Logger log = LoggerFactory.getLogger(RabbitManagementService.class);

  private final AgentConfig agentConfig;
  private final OrderListener orderListener;
  private final Jackson2JsonMessageConverter messageConverter;

  private CachingConnectionFactory connectionFactory;
  private SimpleMessageListenerContainer container;

  public RabbitManagementService(
      AgentConfig agentConfig,
      OrderListener orderListener,
      Jackson2JsonMessageConverter messageConverter) {
    this.agentConfig = agentConfig;
    this.orderListener = orderListener;
    this.messageConverter = messageConverter;
  }

  public synchronized void startConnection() {
    if (container != null && container.isRunning()) {
      log.info("Stopping existing connection...");
      container.stop();
      container = null;
    }

    if (connectionFactory != null) {
      connectionFactory.destroy();
    }

    log.info(
        "Connecting to RabbitMQ at {} with user {}",
        agentConfig.getRabbitHost(),
        agentConfig.getRabbitUsername());

    connectionFactory = new CachingConnectionFactory();
    connectionFactory.setAddresses(agentConfig.getRabbitHost());
    connectionFactory.setUsername(agentConfig.getRabbitUsername());
    connectionFactory.setPassword(agentConfig.getRabbitPassword());

    // Cria o container de escuta dinamicamente
    container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(agentConfig.getQueueName());

    // Adaptador para redirecionar as mensagens para o OrderListener
    MessageListenerAdapter adapter = new MessageListenerAdapter(orderListener, "receiveOrder");
    adapter.setMessageConverter(messageConverter);

    container.setMessageListener(adapter);
    container.start();

    log.info("Agent listening to queue: {}", agentConfig.getQueueName());
  }

  @PreDestroy
  public void shutdown() {
    if (container != null) {
      container.stop();
    }
    if (connectionFactory != null) {
      connectionFactory.destroy();
    }
  }
}

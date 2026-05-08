package dev.thiagooliveira.tablesplit.agent.service;

import dev.thiagooliveira.tablesplit.agent.config.PrintAgentConfig;
import dev.thiagooliveira.tablesplit.agent.listener.TicketListener;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

@Service
public class RabbitManagementService {

  private static final Logger log = LoggerFactory.getLogger(RabbitManagementService.class);

  private final PrintAgentConfig printAgentConfig;
  private final TicketListener ticketListener;
  private final Jackson2JsonMessageConverter messageConverter;

  @org.springframework.beans.factory.annotation.Value("${app.integration.rabbit.order-ticket-exchange}")
  private String exchangeName;

  private CachingConnectionFactory connectionFactory;
  private SimpleMessageListenerContainer container;

  public RabbitManagementService(
      PrintAgentConfig printAgentConfig,
      TicketListener ticketListener,
      Jackson2JsonMessageConverter messageConverter) {
    this.printAgentConfig = printAgentConfig;
    this.ticketListener = ticketListener;
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

    connectionFactory = new CachingConnectionFactory();
    String rabbitAddress = printAgentConfig.getRabbitHost();

    if (rabbitAddress.startsWith("amqp://") || rabbitAddress.startsWith("amqps://")) {
      try {
        connectionFactory.setUri(rabbitAddress);
      } catch (Exception e) {
        log.error("Error setting RabbitMQ URI: {}", e.getMessage());
        // Fallback or rethrow
        throw new RuntimeException("Invalid RabbitMQ URI: " + rabbitAddress, e);
      }
    } else {
      connectionFactory.setAddresses(rabbitAddress);
      connectionFactory.setUsername(printAgentConfig.getRabbitUsername());
      connectionFactory.setPassword(printAgentConfig.getRabbitPassword());
    }

    // Configura o RabbitAdmin para criar a fila/exchange se não existirem
    RabbitAdmin admin = new RabbitAdmin(connectionFactory);

    String queueName = printAgentConfig.getQueueName();
    String routingKey = printAgentConfig.getRoutingKey();

    log.info(
        "Declaring queue: {}, exchange: {}, routingKey: {}", queueName, exchangeName, routingKey);

    Queue queue = new Queue(queueName, true);
    TopicExchange exchange = new TopicExchange(exchangeName);

    admin.declareQueue(queue);
    admin.declareExchange(exchange);
    admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));

    // Cria o container de escuta dinamicamente
    container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(queueName);

    // Adaptador para redirecionar as mensagens para o OrderListener
    MessageListenerAdapter adapter = new MessageListenerAdapter(ticketListener, "receiveOrder");
    adapter.setMessageConverter(messageConverter);

    container.setMessageListener(adapter);
    container.start();

    log.info("Agent listening to queue: {}", queueName);
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

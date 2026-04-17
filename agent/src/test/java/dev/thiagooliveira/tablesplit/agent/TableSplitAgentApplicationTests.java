package dev.thiagooliveira.tablesplit.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class TableSplitAgentApplicationTests {

  @Container @ServiceConnection
  static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management");

  @Test
  void contextLoads() {}
}

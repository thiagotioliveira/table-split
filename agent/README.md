# TableSplit - Local Agent Integration

This component is responsible for listening to **TableSplit** integration queues and automatically injecting orders into the restaurant's local POS (Point of Sale) software database.

## Objective
Eliminate the need for manual order re-entry, reducing errors and operational time.

## Configuration

The agent is configured via an `application.yml` file. The main parameters are:

### RabbitMQ (Cloud or Local)
Configures the message broker address the Agent should listen to.
```yaml
spring:
  rabbitmq:
    addresses: amqp://guest:guest@localhost:5672
```

### Logging
To monitor incoming orders in real-time:
```yaml
logging:
  level:
    dev.thiagooliveira: DEBUG
```

## How to Run

1. Ensure Java 21 is installed.
2. Generate the executable (JAR):
   ```bash
   mvn clean package
   ```
3. Run the Agent:
   ```bash
   java -jar target/table-split-agent.jar
   ```

## Roadmap
- [ ] Implement JDBC integration for Firebird (WinRest).
- [ ] Implement JDBC integration for MySQL/SQL Server (Zonesoft/Others).
- [ ] Configure automatic injection of service charges and discounts.

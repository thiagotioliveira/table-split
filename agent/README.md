# TableSplit - Local Agent Integration

This component is responsible for listening to **TableSplit** integration queues and automatically printing orders received from the Web application.

## Objective
Provide real-time order printing at the restaurant's kitchen or bar, ensuring that orders are processed immediately after being placed by the customer.

## Features
- **RabbitMQ Integration**: Receives `TicketCreatedEvent` via a topic exchange.
- **Automated Printing**: Sends the formatted order directly to the default system printer using Java's Printing API.
- **Robustness**: Handles different class packages via Jackson's `INFERRED` type mapping.

## Configuration

The agent is configured via an `application.yml` file.

### RabbitMQ
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
    dev.thiagooliveira: INFO
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
- [x] RabbitMQ Listener Integration.
- [x] Java Printing API implementation.
- [ ] Support for selecting specific printers via configuration.
- [ ] Network printer support (via IP/Port).
- [ ] Custom receipt templates.


package dev.thiagooliveira.tablesplit.web.api.agent;

import java.util.UUID;

public record PrintAgentConfigDTO(
    UUID restaurantId,
    String restaurantName,
    String rabbitHost,
    String rabbitUsername,
    String rabbitPassword,
    String queueName,
    String routingKey) {}

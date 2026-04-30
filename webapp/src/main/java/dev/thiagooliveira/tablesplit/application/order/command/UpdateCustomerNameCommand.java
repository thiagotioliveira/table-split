package dev.thiagooliveira.tablesplit.application.order.command;

import java.util.UUID;

public record UpdateCustomerNameCommand(UUID customerId, String name) {}

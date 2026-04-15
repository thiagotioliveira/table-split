package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.UUID;

public record UpdateCustomerNameRequest(UUID customerId, String name) {}

package dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model;

public record UpdatePreferencesRequest(
    String endpoint, boolean notifyNewOrders, boolean notifyCallWaiter) {}

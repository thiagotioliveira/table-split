package dev.thiagooliveira.tablesplit.infrastructure.notification.api.model;

public record UpdatePreferencesRequest(
    String endpoint,
    boolean notifyNewOrders,
    boolean notifyCallWaiter,
    boolean notifyOrderClosed) {}

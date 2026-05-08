package dev.thiagooliveira.tablesplit.infrastructure.notification.api.model;

public record SubscriptionData(String endpoint, String p256dh, String auth) {}

package dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model;

public record SubscriptionData(String endpoint, String p256dh, String auth) {}

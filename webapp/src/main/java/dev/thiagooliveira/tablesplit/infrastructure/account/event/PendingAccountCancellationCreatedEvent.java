package dev.thiagooliveira.tablesplit.infrastructure.account.event;

public record PendingAccountCancellationCreatedEvent(
    String email,
    String code,
    String firstName,
    String language,
    String baseUrl,
    String restaurantName) {}

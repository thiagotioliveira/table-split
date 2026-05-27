package dev.thiagooliveira.tablesplit.infrastructure.account.event;

public record AccountCancelledEvent(
    String email, String firstName, String language, String restaurantName) {}

package dev.thiagooliveira.tablesplit.infrastructure.web.login.event;

public record PendingRegistrationCreatedEvent(
    String email, String code, String firstName, String language, String baseUrl) {}

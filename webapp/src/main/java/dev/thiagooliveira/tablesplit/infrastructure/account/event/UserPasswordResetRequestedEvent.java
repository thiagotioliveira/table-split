package dev.thiagooliveira.tablesplit.infrastructure.account.event;

import java.util.UUID;

public record UserPasswordResetRequestedEvent(
    UUID token, String email, String firstName, String baseUrl) {}

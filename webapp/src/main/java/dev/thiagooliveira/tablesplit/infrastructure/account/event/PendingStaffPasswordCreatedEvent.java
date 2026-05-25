package dev.thiagooliveira.tablesplit.infrastructure.account.event;

import java.util.UUID;

public record PendingStaffPasswordCreatedEvent(
    UUID token,
    String email,
    String firstName,
    String restaurantName,
    String restaurantSlug,
    String defaultLanguage,
    String baseUrl) {}

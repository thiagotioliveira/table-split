package dev.thiagooliveira.tablesplit.infrastructure.account.event;

import java.util.UUID;

public record StaffPasswordResetRequestedEvent(
    UUID token,
    String email,
    String firstName,
    String restaurantName,
    String restaurantSlug,
    String baseUrl) {}

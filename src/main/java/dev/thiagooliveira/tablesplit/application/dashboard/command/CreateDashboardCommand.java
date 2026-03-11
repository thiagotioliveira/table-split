package dev.thiagooliveira.tablesplit.application.dashboard.command;

import java.util.UUID;

public record CreateDashboardCommand(
    UUID accountId, UUID userId, String firstName, String lastName, String email) {}

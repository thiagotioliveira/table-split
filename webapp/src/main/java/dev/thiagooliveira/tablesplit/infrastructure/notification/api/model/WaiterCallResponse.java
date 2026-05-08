package dev.thiagooliveira.tablesplit.infrastructure.notification.api.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public record WaiterCallResponse(UUID id, String tableCod, ZonedDateTime createdAt, int count) {}

package dev.thiagooliveira.tablesplit.infrastructure.web.api.notification.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public record WaiterCallResponse(UUID id, String tableCod, ZonedDateTime createdAt, int count) {}

package dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.projection;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.UUID;

public record CategoryProjection(
    UUID id,
    UUID restaurantId,
    Integer numOrder,
    boolean active,
    String nameTranslation,
    Language language) {}

package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.UUID;

public record CategoryDto(
    UUID id,
    UUID restaurantId,
    Integer numOrder,
    boolean active,
    String nameTranslation,
    Language language) {}

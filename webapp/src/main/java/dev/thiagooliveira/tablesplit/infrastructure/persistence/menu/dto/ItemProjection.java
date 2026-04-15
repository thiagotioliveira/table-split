package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemEntity;

public record ItemProjection(
    ItemEntity item, String nameTranslation, String descriptionTranslation, Language language) {}

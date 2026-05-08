package dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.projection;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemEntity;

public record ItemProjection(
    ItemEntity item, String nameTranslation, String descriptionTranslation, Language language) {}

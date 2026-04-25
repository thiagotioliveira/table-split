package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion;
import dev.thiagooliveira.tablesplit.domain.menu.ItemTag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateItemCommand(
    UUID id,
    UUID categoryId,
    List<UUID> imageIdsToKeep,
    ImageCommand images,
    Map<Language, String> name,
    Map<Language, String> description,
    BigDecimal price,
    List<ItemTag> tags,
    boolean available,
    Map<Language, List<ItemQuestion>> questions) {}

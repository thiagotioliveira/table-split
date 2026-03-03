package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CreateItemCommand(
    UUID categoryId,
    Map<Language, String> name,
    Map<Language, String> description,
    BigDecimal price) {}

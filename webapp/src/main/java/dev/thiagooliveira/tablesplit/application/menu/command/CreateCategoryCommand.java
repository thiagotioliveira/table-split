package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Map;

public record CreateCategoryCommand(Map<Language, String> name, int order) {}

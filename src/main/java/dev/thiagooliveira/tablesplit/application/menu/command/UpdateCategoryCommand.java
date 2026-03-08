package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.util.Map;

public record UpdateCategoryCommand(Map<Language, String> name, int order) {}

package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.UUID;

public record PromotionCategoryModel(UUID id, String name) {
  public static PromotionCategoryModel from(Category category, Language language) {
    return new PromotionCategoryModel(category.getId(), category.getName().get(language));
  }
}

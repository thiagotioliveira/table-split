package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;
import java.util.UUID;

public class GetCategory {

  public final CategoryRepository categoryRepository;

  public GetCategory(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<Category> execute(UUID restaurantId, List<Language> languages) {
    return this.categoryRepository.findAll(restaurantId, languages);
  }

  public long count(UUID restaurantId) {
    return this.categoryRepository.count(restaurantId);
  }

  public long countActive(UUID restaurantId) {
    return this.categoryRepository.countActive(restaurantId);
  }

  public long countInactive(UUID restaurantId) {
    return this.categoryRepository.countInactive(restaurantId);
  }
}

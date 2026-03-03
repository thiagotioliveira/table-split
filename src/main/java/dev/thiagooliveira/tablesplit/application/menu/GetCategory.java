package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetCategory {

  public final CategoryRepository categoryRepository;

  public GetCategory(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<Category> execute(UUID restaurantId) {
    return this.categoryRepository.getAll(restaurantId);
  }

  public Optional<Category> execute(UUID restaurantId, UUID categoryId) {
    return this.categoryRepository.getById(categoryId);
  }
}

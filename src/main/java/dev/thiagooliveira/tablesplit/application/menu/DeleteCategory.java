package dev.thiagooliveira.tablesplit.application.menu;

import java.util.UUID;

public class DeleteCategory {

  public final CategoryRepository categoryRepository;

  public DeleteCategory(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void execute(UUID restaurantId, UUID categoryId) {
    this.categoryRepository.delete(categoryId);
  }
}

package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.CategoryRepository;
import java.util.UUID;

public class DeleteCategory {

  private final CategoryRepository categoryRepository;

  public DeleteCategory(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID categoryId) {
    var category = this.categoryRepository.findById(categoryId).orElseThrow();
    if (!category.getAccountId().equals(accountId)
        || !category.getRestaurantId().equals(restaurantId)) {
      throw new IllegalArgumentException("Access denied");
    }
    category.delete();
    this.categoryRepository.save(category);
  }
}

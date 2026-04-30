package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.CategoryRepository;
import java.util.UUID;

public class UpdateCategory {

  private final CategoryRepository categoryRepository;

  public UpdateCategory(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category execute(
      UUID accountId, UUID restaurantId, UUID categoryId, UpdateCategoryCommand command) {
    var category = this.categoryRepository.findById(categoryId).orElseThrow();
    if (!category.getAccountId().equals(accountId)
        || !category.getRestaurantId().equals(restaurantId)) {
      throw new IllegalArgumentException("Access denied");
    }
    category.setName(command.name());
    category.setOrder(command.order());
    this.categoryRepository.save(category);

    return category;
  }
}

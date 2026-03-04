package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.dto.UpdateCategoryCommand;
import java.util.UUID;

public class UpdateCategory {

  private final GetCategory getCategory;
  private final CategoryRepository categoryRepository;

  public UpdateCategory(GetCategory getCategory, CategoryRepository categoryRepository) {
    this.getCategory = getCategory;
    this.categoryRepository = categoryRepository;
  }

  public void execute(UUID restaurantId, UUID categoryId, UpdateCategoryCommand command) {
    var category = this.getCategory.execute(restaurantId, categoryId).orElseThrow();
    category.setOrder(command.order());
    category.setName(command.name());
    this.categoryRepository.save(category);
  }
}

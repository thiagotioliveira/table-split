package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.UUID;

public class CreateCategory {

  private final CategoryRepository categoryRepository;

  public CreateCategory(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void execute(UUID restaurantId, CreateCategoryCommand command) {
    var category = new Category();
    category.setId(UUID.randomUUID());
    category.setRestaurantId(restaurantId);
    category.setOrder(command.order());
    category.setName(command.name());
    this.categoryRepository.save(category);
  }
}

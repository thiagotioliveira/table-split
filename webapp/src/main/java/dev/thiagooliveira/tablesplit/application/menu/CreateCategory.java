package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitType;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.CategoryRepository;
import java.util.UUID;

public class CreateCategory {

  private final CategoryRepository categoryRepository;
  private final PlanLimitValidator planLimitValidator;

  public CreateCategory(
      CategoryRepository categoryRepository, PlanLimitValidator planLimitValidator) {
    this.categoryRepository = categoryRepository;
    this.planLimitValidator = planLimitValidator;
  }

  public Category execute(UUID accountId, UUID restaurantId, CreateCategoryCommand command) {
    this.planLimitValidator.validate(
        accountId, PlanLimitType.CATEGORIES, this.categoryRepository.count(restaurantId));

    var category = Category.create(accountId, restaurantId);
    category.setOrder(command.order());
    category.setName(command.name());
    this.categoryRepository.save(category);

    return category;
  }
}

package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitType;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.event.CategoryCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.UUID;

public class CreateCategory {

  private final EventPublisher eventPublisher;
  private final CategoryRepository categoryRepository;
  private final PlanLimitValidator planLimitValidator;

  public CreateCategory(
      EventPublisher eventPublisher,
      CategoryRepository categoryRepository,
      PlanLimitValidator planLimitValidator) {
    this.eventPublisher = eventPublisher;
    this.categoryRepository = categoryRepository;
    this.planLimitValidator = planLimitValidator;
  }

  public Category execute(UUID accountId, UUID restaurantId, CreateCategoryCommand command) {
    this.planLimitValidator.validate(
        accountId, PlanLimitType.CATEGORIES, this.categoryRepository.count(restaurantId));

    var category = new Category();
    category.setId(UUID.randomUUID());
    category.setRestaurantId(restaurantId);
    category.setOrder(command.order());
    category.setName(command.name());
    this.categoryRepository.save(category);

    this.eventPublisher.publishEvent(new CategoryCreatedEvent(accountId, category.getId()));
    return category;
  }
}

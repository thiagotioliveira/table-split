package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.event.CategoryCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.UUID;

public class CreateCategory {

  private final EventPublisher eventPublisher;
  private final CategoryRepository categoryRepository;

  public CreateCategory(EventPublisher eventPublisher, CategoryRepository categoryRepository) {
    this.eventPublisher = eventPublisher;
    this.categoryRepository = categoryRepository;
  }

  public void execute(UUID accountId, UUID restaurantId, CreateCategoryCommand command) {
    var category = new Category();
    category.setId(UUID.randomUUID());
    category.setRestaurantId(restaurantId);
    category.setOrder(command.order());
    category.setName(command.name());
    this.categoryRepository.save(category);

    var total = this.categoryRepository.count();
    var totalActive = this.categoryRepository.countActive();
    var totalInactive = this.categoryRepository.countInactive();
    this.eventPublisher.publishEvent(
        new CategoryCreatedEvent(accountId, category.getId(), total, totalActive, totalInactive));
  }
}

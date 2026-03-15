package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.event.CategoryUpdatedEvent;
import java.util.UUID;

public class UpdateCategory {

  private final EventPublisher eventPublisher;
  private final CategoryRepository categoryRepository;

  public UpdateCategory(EventPublisher eventPublisher, CategoryRepository categoryRepository) {
    this.eventPublisher = eventPublisher;
    this.categoryRepository = categoryRepository;
  }

  public void execute(
      UUID accountId, UUID restaurantId, UUID categoryId, UpdateCategoryCommand command) {
    var category = this.categoryRepository.findById(categoryId).orElseThrow();
    category.setOrder(command.order());
    category.setName(command.name());
    this.categoryRepository.save(category);
    this.eventPublisher.publishEvent(new CategoryUpdatedEvent(accountId, categoryId));
  }
}

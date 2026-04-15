package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.CategoryDeletedEvent;
import java.util.UUID;

public class DeleteCategory {

  private final EventPublisher eventPublisher;
  public final CategoryRepository categoryRepository;

  public DeleteCategory(EventPublisher eventPublisher, CategoryRepository categoryRepository) {
    this.eventPublisher = eventPublisher;
    this.categoryRepository = categoryRepository;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID categoryId) {
    this.categoryRepository.delete(categoryId);
    this.eventPublisher.publishEvent(new CategoryDeletedEvent(accountId, categoryId));
  }
}

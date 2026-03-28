package dev.thiagooliveira.tablesplit.infrastructure.config.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuConfig {

  @Bean
  public GetCategory getCategory(CategoryRepository categoryRepository) {
    return new GetCategory(categoryRepository);
  }

  @Bean
  public CreateCategory createCategory(
      EventPublisher eventPublisher, CategoryRepository categoryRepository) {
    return new CreateCategory(eventPublisher, categoryRepository);
  }

  @Bean
  public UpdateCategory updateCategory(
      EventPublisher eventPublisher, CategoryRepository categoryRepository) {
    return new UpdateCategory(eventPublisher, categoryRepository);
  }

  @Bean
  public DeleteCategory deleteCategory(
      EventPublisher eventPublisher, CategoryRepository categoryRepository) {
    return new DeleteCategory(eventPublisher, categoryRepository);
  }

  @Bean
  public GetItem getItem(ItemRepository itemRepository, PromotionRepository promotionRepository) {
    return new GetItem(itemRepository, promotionRepository);
  }

  @Bean
  public UpdateItem updateItem(
      EventPublisher eventPublisher,
      ImageStorage imageStorage,
      ItemRepository itemRepository,
      @Value("${app.menu.item.image.max-size:1048576}") long maxImageSize) {
    return new UpdateItem(eventPublisher, imageStorage, itemRepository, maxImageSize);
  }

  @Bean
  public CreateItem createItem(
      EventPublisher eventPublisher,
      ImageStorage imageStorage,
      ItemRepository itemRepository,
      @Value("${app.menu.item.image.max-size:1048576}") long maxImageSize) {
    return new CreateItem(eventPublisher, imageStorage, itemRepository, maxImageSize);
  }

  @Bean
  public DeleteItem deleteItem(EventPublisher eventPublisher, ItemRepository itemRepository) {
    return new DeleteItem(eventPublisher, itemRepository);
  }
}

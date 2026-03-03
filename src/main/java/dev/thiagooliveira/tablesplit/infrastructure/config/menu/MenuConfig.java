package dev.thiagooliveira.tablesplit.infrastructure.config.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuConfig {

  @Bean
  public GetCategory getCategory(CategoryRepository categoryRepository) {
    return new GetCategory(categoryRepository);
  }

  @Bean
  public CreateCategory createCategory(CategoryRepository categoryRepository) {
    return new CreateCategory(categoryRepository);
  }

  @Bean
  public UpdateCategory updateCategory(
      GetCategory getCategory, CategoryRepository categoryRepository) {
    return new UpdateCategory(getCategory, categoryRepository);
  }

  @Bean
  public DeleteCategory deleteCategory(CategoryRepository categoryRepository) {
    return new DeleteCategory(categoryRepository);
  }

  @Bean
  public GetItem getItem(ItemRepository itemRepository) {
    return new GetItem(itemRepository);
  }

  @Bean
  public UpdateItem updateItem(GetItem getItem, ItemRepository itemRepository) {
    return new UpdateItem(getItem, itemRepository);
  }

  @Bean
  public CreateItem createItem(ItemRepository itemRepository) {
    return new CreateItem(itemRepository);
  }

  @Bean
  public DeleteItem deleteItem(ItemRepository itemRepository) {
    return new DeleteItem(itemRepository);
  }
}

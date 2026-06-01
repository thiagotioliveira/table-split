package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.CategoryRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateCategoryTest {

  private CategoryRepository categoryRepository;
  private UpdateCategory updateCategory;

  @BeforeEach
  void setUp() {
    categoryRepository = mock(CategoryRepository.class);
    updateCategory = new UpdateCategory(categoryRepository);
  }

  @Test
  void shouldUpdateCategorySuccessfully() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    Category category = new Category();
    category.setId(categoryId);
    category.setAccountId(accountId);
    category.setRestaurantId(restaurantId);

    UpdateCategoryCommand command = new UpdateCategoryCommand(Map.of(Language.PT, "Drinks"), 2);

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    Category result = updateCategory.execute(accountId, restaurantId, categoryId, command);

    assertNotNull(result);
    assertEquals(command.name(), result.getName());
    assertEquals(2, result.getOrder());
    verify(categoryRepository).save(category);
  }

  @Test
  void shouldThrowExceptionWhenAccessDenied() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    Category category = new Category();
    category.setId(categoryId);
    category.setAccountId(UUID.randomUUID()); // different account
    category.setRestaurantId(restaurantId);

    UpdateCategoryCommand command = new UpdateCategoryCommand(Map.of(Language.PT, "Drinks"), 2);

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    assertThrows(
        IllegalArgumentException.class,
        () -> updateCategory.execute(accountId, restaurantId, categoryId, command));
  }
}

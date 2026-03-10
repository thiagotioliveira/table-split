package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  List<Category> findAll(UUID restaurantId);

  Optional<Category> findById(UUID categoryId);

  void save(Category category);

  void delete(UUID categoryId);

  long count();

  long countActive();

  long countInactive();
}

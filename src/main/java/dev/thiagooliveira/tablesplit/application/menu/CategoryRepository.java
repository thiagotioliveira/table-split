package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  List<Category> getAll(UUID restaurantId);

  Optional<Category> getById(UUID categoryId);

  void save(Category category);

  void delete(UUID categoryId);
}

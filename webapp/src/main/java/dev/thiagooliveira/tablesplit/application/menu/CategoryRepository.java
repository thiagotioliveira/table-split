package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  List<Category> findByRestaurantId(UUID restaurantId);

  List<Category> findAll(UUID restaurantId, List<Language> languages);

  Optional<Category> findById(UUID categoryId);

  void save(Category category);

  void delete(UUID categoryId);

  long count(UUID restaurantId);

  long countActive(UUID restaurantId);

  long countInactive(UUID restaurantId);
}

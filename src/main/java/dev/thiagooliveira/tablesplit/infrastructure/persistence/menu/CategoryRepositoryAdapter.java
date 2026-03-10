package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.CategoryRepository;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CategoryRepositoryAdapter implements CategoryRepository {

  private final CategoryJpaRepository categoryJpaRepository;

  public CategoryRepositoryAdapter(CategoryJpaRepository categoryJpaRepository) {
    this.categoryJpaRepository = categoryJpaRepository;
  }

  @Override
  public List<Category> findAll(UUID restaurantId) {
    return this.categoryJpaRepository.findAllByRestaurantIdOrderByNumOrder(restaurantId).stream()
        .map(CategoryEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Category> findById(UUID categoryId) {
    return this.categoryJpaRepository.findById(categoryId).map(CategoryEntity::toDomain);
  }

  @Override
  public void save(Category category) {
    this.categoryJpaRepository.save(CategoryEntity.fromDomain(category));
  }

  @Override
  public void delete(UUID categoryId) {
    this.categoryJpaRepository.deleteById(categoryId);
  }

  @Override
  public long count() {
    return this.categoryJpaRepository.count();
  }

  @Override
  public long countActive() {
    return this.categoryJpaRepository.count(); // TODO
  }

  @Override
  public long countInactive() {
    return 0; // TODO
  }
}

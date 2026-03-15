package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.CategoryRepository;
import dev.thiagooliveira.tablesplit.domain.common.Language;
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
  public List<Category> findAll(UUID restaurantId, List<Language> languages) {
    var dtos =
        this.categoryJpaRepository.findAllByRestaurantIdAndLanguagesOrderByNumOrder(
            restaurantId, languages);
    java.util.Map<UUID, Category> categoryMap = new java.util.LinkedHashMap<>();
    for (var dto : dtos) {
      var domain =
          categoryMap.computeIfAbsent(
              dto.id(),
              id -> {
                var c = new Category();
                c.setId(dto.id());
                c.setRestaurantId(dto.restaurantId());
                c.setOrder(dto.numOrder());
                c.setName(new java.util.HashMap<>());
                return c;
              });
      domain.getName().put(dto.language(), dto.nameTranslation());
    }
    return new java.util.ArrayList<>(categoryMap.values());
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
  public long count(UUID restaurantId) {
    return this.categoryJpaRepository.countByRestaurantId(restaurantId);
  }

  @Override
  public long countActive(UUID restaurantId) {
    return this.categoryJpaRepository.countByRestaurantIdAndActiveTrue(restaurantId);
  }

  @Override
  public long countInactive(UUID restaurantId) {
    return this.categoryJpaRepository.countByRestaurantIdAndActiveFalse(restaurantId);
  }
}

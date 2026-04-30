package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.CategoryRepository;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CategoryRepositoryAdapter implements CategoryRepository {

  private final CategoryJpaRepository categoryJpaRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;

  public CategoryRepositoryAdapter(
      CategoryJpaRepository categoryJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher) {
    this.categoryJpaRepository = categoryJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public List<Category> findByRestaurantId(UUID restaurantId) {
    return this.categoryJpaRepository.findByRestaurantId(restaurantId).stream()
        .map(this::toDomainWithAccount)
        .toList();
  }

  private Category toDomainWithAccount(CategoryEntity entity) {
    Category domain = entity.toDomain();
    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            domain.getRestaurantId());
    if (cachedAccountId != null) {
      domain.setAccountId(cachedAccountId);
    } else if (entity.getRestaurant() != null) {
      domain.setAccountId(entity.getRestaurant().getAccountId());
    }
    return domain;
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
    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            restaurantId);

    categoryMap
        .values()
        .forEach(
            c -> {
              if (cachedAccountId != null) {
                c.setAccountId(cachedAccountId);
              }
            });

    return new java.util.ArrayList<>(categoryMap.values());
  }

  @Override
  public Optional<Category> findById(UUID categoryId) {
    return this.categoryJpaRepository.findById(categoryId).map(this::toDomainWithAccount);
  }

  @Override
  public void save(Category category) {
    var entity =
        this.categoryJpaRepository
            .findById(category.getId())
            .orElseGet(
                () -> {
                  var newEntity = new CategoryEntity();
                  newEntity.setId(category.getId());
                  newEntity.setRestaurantId(category.getRestaurantId());
                  return newEntity;
                });
    entity.setNumOrder(category.getOrder());
    entity.setName(LocalizedTextEntity.fromMap(category.getName()));
    entity.setActive(true);
    this.categoryJpaRepository.save(entity);

    // Ensure accountId is populated for events
    if (category.getAccountId() == null) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              category.getRestaurantId());
      category.setAccountId(cachedAccountId);
    }

    category.getDomainEvents().forEach(eventPublisher::publishEvent);
    category.clearEvents();
  }

  @Override
  public void delete(UUID categoryId) {
    var category = findById(categoryId).orElseThrow();
    category.delete();

    this.categoryJpaRepository.deleteById(categoryId);

    category.getDomainEvents().forEach(eventPublisher::publishEvent);
    category.clearEvents();
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

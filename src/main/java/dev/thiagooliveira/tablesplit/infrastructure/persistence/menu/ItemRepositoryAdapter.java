package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ItemRepositoryAdapter implements ItemRepository {

  private final ItemJpaRepository itemJpaRepository;

  public ItemRepositoryAdapter(ItemJpaRepository itemJpaRepository) {
    this.itemJpaRepository = itemJpaRepository;
  }

  @Override
  public Optional<Item> findById(UUID id) {
    return this.itemJpaRepository.findById(id).map(ItemEntity::toDomain);
  }

  @Override
  public List<Item> findByRestaurantId(UUID restaurantId) {
    return this.itemJpaRepository.findByCategoryRestaurantId(restaurantId).stream()
        .map(ItemEntity::toDomain)
        .toList();
  }

  @Override
  public List<Item> findAll(UUID restaurantId, List<Language> languages) {
    var projections =
        this.itemJpaRepository.findAllByCategoryRestaurantIdAndLanguages(restaurantId, languages);
    java.util.Map<UUID, Item> itemMap = new java.util.LinkedHashMap<>();
    for (var projection : projections) {
      var domain =
          itemMap.computeIfAbsent(
              projection.item().getId(),
              id -> {
                var i = projection.item().toDomain();
                i.setName(new java.util.HashMap<>());
                i.setDescription(new java.util.HashMap<>());
                return i;
              });
      if (projection.nameTranslation() != null) {
        domain.getName().put(projection.language(), projection.nameTranslation());
      }
      if (projection.descriptionTranslation() != null) {
        domain.getDescription().put(projection.language(), projection.descriptionTranslation());
      }
    }
    return new java.util.ArrayList<>(itemMap.values());
  }

  @Override
  public Item save(Item item) {
    return this.itemJpaRepository.save(ItemEntity.fromDomain(item)).toDomain();
  }

  @Override
  public void delete(UUID itemId) {
    this.itemJpaRepository.deleteById(itemId);
  }

  @Override
  public long count(UUID restaurantId) {
    return this.itemJpaRepository.countByCategoryRestaurantId(restaurantId);
  }

  @Override
  public long countActive(UUID restaurantId) {
    return this.itemJpaRepository.countByCategoryRestaurantIdAndActiveTrue(restaurantId);
  }

  @Override
  public long countInactive(UUID restaurantId) {
    return this.itemJpaRepository.countByCategoryRestaurantIdAndActiveFalse(restaurantId);
  }
}

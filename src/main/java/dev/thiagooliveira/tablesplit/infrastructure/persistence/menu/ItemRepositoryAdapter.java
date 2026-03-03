package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
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
  public Optional<Item> getById(UUID id) {
    return this.itemJpaRepository.findById(id).map(ItemEntity::toDomain);
  }

  @Override
  public List<Item> getAll(UUID restaurantId) {
    return this.itemJpaRepository.findAllByCategoryRestaurantId(restaurantId).stream()
        .map(ItemEntity::toDomain)
        .toList();
  }

  @Override
  public void save(Item item) {
    this.itemJpaRepository.save(ItemEntity.fromDomain(item));
  }

  @Override
  public void delete(UUID itemId) {
    this.itemJpaRepository.deleteById(itemId);
  }
}

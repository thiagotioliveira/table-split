package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.ItemImageRepository;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ItemImageRepositoryAdapter implements ItemImageRepository {

  private final ItemImageJpaRepository itemImageJpaRepository;

  public ItemImageRepositoryAdapter(ItemImageJpaRepository itemImageJpaRepository) {
    this.itemImageJpaRepository = itemImageJpaRepository;
  }

  @Override
  public void save(ItemImage itemImage) {
    this.itemImageJpaRepository.save(ItemImageEntity.fromDomain(itemImage));
  }

  @Override
  public void delete(UUID id) {
    this.itemImageJpaRepository.deleteById(id);
  }
}

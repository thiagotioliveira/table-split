package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImageRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ItemImageRepositoryAdapter implements ItemImageRepository {

  private final ItemImageJpaRepository itemImageJpaRepository;
  private final ItemImageEntityMapper mapper;

  public ItemImageRepositoryAdapter(
      ItemImageJpaRepository itemImageJpaRepository, ItemImageEntityMapper mapper) {
    this.itemImageJpaRepository = itemImageJpaRepository;
    this.mapper = mapper;
  }

  @Override
  public void save(ItemImage itemImage) {
    this.itemImageJpaRepository.save(mapper.toEntity(itemImage));
  }

  @Override
  public void delete(UUID id) {
    this.itemImageJpaRepository.deleteById(id);
  }
}

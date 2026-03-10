package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetItem {

  private final ItemRepository itemRepository;

  public GetItem(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<Item> execute(UUID restaurantId) {
    return this.itemRepository.findAll(restaurantId);
  }

  public Optional<Item> execute(UUID restaurantId, UUID itemId) {
    return this.itemRepository.findById(itemId);
  }
}

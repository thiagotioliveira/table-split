package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.List;
import java.util.UUID;

public class GetItem {

  private final ItemRepository itemRepository;

  public GetItem(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<Item> execute(UUID restaurantId, List<Language> languages) {
    return this.itemRepository.findAll(restaurantId, languages);
  }

  public long count(UUID restaurantId) {
    return this.itemRepository.count(restaurantId);
  }

  public long countActive(UUID restaurantId) {
    return this.itemRepository.countActive(restaurantId);
  }

  public long countInactive(UUID restaurantId) {
    return this.itemRepository.countInactive(restaurantId);
  }
}

package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ItemImageStorage;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import java.util.UUID;

public class DeleteItem {

  private final ItemRepository itemRepository;
  private final ItemImageStorage itemImageStorage;

  public DeleteItem(ItemRepository itemRepository, ItemImageStorage itemImageStorage) {
    this.itemRepository = itemRepository;
    this.itemImageStorage = itemImageStorage;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID itemId) {
    var item = this.itemRepository.findById(itemId).orElseThrow();
    if (!item.getAccountId().equals(accountId) || !item.getRestaurantId().equals(restaurantId)) {
      throw new IllegalArgumentException("Access denied");
    }
    this.itemRepository.delete(itemId);

    for (var img : item.getImages()) {
      this.itemImageStorage.delete(accountId, restaurantId, itemId, img.getId());
    }
    item.delete();
  }
}

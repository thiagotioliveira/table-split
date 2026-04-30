package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import java.util.UUID;

public class DeleteItem {

  private final ItemRepository itemRepository;
  private final ImageStorage imageStorage;

  public DeleteItem(ItemRepository itemRepository, ImageStorage imageStorage) {
    this.itemRepository = itemRepository;
    this.imageStorage = imageStorage;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID itemId) {
    var item = this.itemRepository.findById(itemId).orElseThrow();
    if (!item.getAccountId().equals(accountId) || !item.getRestaurantId().equals(restaurantId)) {
      throw new IllegalArgumentException("Access denied");
    }
    item.delete();
    this.itemRepository.save(item);

    for (var img : item.getImages()) {
      this.imageStorage.deleteItem(accountId, restaurantId, itemId, img.getId());
    }
  }
}

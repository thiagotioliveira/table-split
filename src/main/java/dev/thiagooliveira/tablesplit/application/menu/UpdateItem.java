package dev.thiagooliveira.tablesplit.application.menu;

import java.util.UUID;

public class UpdateItem {

  private final GetItem getItem;
  private final ItemRepository itemRepository;

  public UpdateItem(GetItem getItem, ItemRepository itemRepository) {
    this.getItem = getItem;
    this.itemRepository = itemRepository;
  }

  public void execute(UUID restaurantId, UUID itemId, UpdateItemCommand command) {
    var item = this.getItem.execute(restaurantId, itemId).orElseThrow();
    item.setPrice(command.price());
    item.setDescription(command.description());
    item.setName(command.name());
    item.setCategoryId(command.categoryId());
    this.itemRepository.save(item);
  }
}

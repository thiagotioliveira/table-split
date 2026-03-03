package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.UUID;

public class CreateItem {

  private final ItemRepository itemRepository;

  public CreateItem(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public void execute(UUID restaurantId, CreateItemCommand command) {
    var item = new Item();
    item.setId(UUID.randomUUID());
    item.setName(command.name());
    item.setDescription(command.description());
    item.setCategoryId(command.categoryId());
    item.setPrice(command.price());
    this.itemRepository.save(item);
  }
}

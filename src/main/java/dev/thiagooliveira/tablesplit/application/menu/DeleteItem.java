package dev.thiagooliveira.tablesplit.application.menu;

import java.util.UUID;

public class DeleteItem {

  public final ItemRepository itemRepository;

  public DeleteItem(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public void execute(UUID itemId) {
    this.itemRepository.delete(itemId);
  }
}

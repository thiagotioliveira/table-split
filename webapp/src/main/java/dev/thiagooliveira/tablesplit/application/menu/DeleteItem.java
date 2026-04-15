package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.domain.event.ItemDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.UUID;

public class DeleteItem {

  private final EventPublisher eventPublisher;
  public final ItemRepository itemRepository;
  private final ImageStorage imageStorage;

  public DeleteItem(
      EventPublisher eventPublisher, ItemRepository itemRepository, ImageStorage imageStorage) {
    this.eventPublisher = eventPublisher;
    this.itemRepository = itemRepository;
    this.imageStorage = imageStorage;
  }

  public void execute(UUID accountId, UUID itemId) {
    Item item = this.itemRepository.findById(itemId).orElseThrow();

    boolean hasTicketItems = this.itemRepository.existsInTicketItems(itemId);

    if (hasTicketItems) {
      // Exclusão lógica - deletar imagens do storage
      if (item.getImages() != null) {
        item.getImages()
            .forEach(
                image ->
                    this.imageStorage.deleteItem(
                        accountId, item.getRestaurantId(), itemId, image.getId()));
      }
    }

    this.itemRepository.delete(itemId);
    this.eventPublisher.publishEvent(new ItemDeletedEvent(accountId, itemId));
  }
}

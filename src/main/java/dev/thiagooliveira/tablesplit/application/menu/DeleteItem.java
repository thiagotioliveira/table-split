package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.ItemDeletedEvent;
import java.util.UUID;

public class DeleteItem {

  private final EventPublisher eventPublisher;
  public final ItemRepository itemRepository;

  public DeleteItem(EventPublisher eventPublisher, ItemRepository itemRepository) {
    this.eventPublisher = eventPublisher;
    this.itemRepository = itemRepository;
  }

  public void execute(UUID accountId, UUID itemId) {
    this.itemRepository.delete(itemId);
    this.eventPublisher.publishEvent(new ItemDeletedEvent(accountId, itemId));
  }
}

package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.event.ItemUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import java.util.ArrayList;
import java.util.UUID;

public class UpdateItem {
  private final EventPublisher eventPublisher;
  private final ImageStorage imageStorage;
  private final ItemRepository itemRepository;
  private final long maxImageSize;

  public UpdateItem(
      EventPublisher eventPublisher,
      ImageStorage imageStorage,
      ItemRepository itemRepository,
      long maxImageSize) {
    this.eventPublisher = eventPublisher;
    this.imageStorage = imageStorage;
    this.itemRepository = itemRepository;
    this.maxImageSize = maxImageSize;
  }

  public void execute(UUID accountId, UUID restaurantId, UUID itemId, UpdateItemCommand command) {
    var item = this.itemRepository.findById(itemId).orElseThrow();
    item.setPrice(command.price());
    item.setDescription(command.description());
    item.setName(command.name());
    item.setAvailable(command.available());
    item.setCategory(new Category());
    item.getCategory().setId(command.categoryId());

    var imageIdsToKeep =
        command.imageIdsToKeep() == null ? new ArrayList<>() : command.imageIdsToKeep();

    item.getImages()
        .removeIf(
            img -> {
              if (!imageIdsToKeep.contains(img.getId())) {
                imageStorage.deleteItem(accountId, restaurantId, itemId, img.getId());
                return true;
              }
              return false;
            });

    if (command.images() != null && command.images().newImages() != null) {
      for (ImageData i : command.images().newImages()) {
        if (i.content() != null && i.content().length > this.maxImageSize) {
          throw new dev.thiagooliveira.tablesplit.application.exception.ImageSizeExceededException(
              "error.menu.item.image.size");
        }
        var image = new ItemImage();
        image.setId(UUID.randomUUID());

        boolean hasMain =
            item.getImages().stream()
                .anyMatch(img -> imageIdsToKeep.contains(img.getId()) && img.isMain());
        image.setMain(!hasMain && command.images().newImages().indexOf(i) == 0);
        image.setItemId(item.getId());
        image.setName(
            imageStorage.uploadItem(
                i, accountId, item.getRestaurantId(), item.getId(), image.getId()));
        item.getImages().add(image);
      }
    }
    item = this.itemRepository.save(item);

    this.eventPublisher.publishEvent(new ItemUpdatedEvent(accountId, item.getId()));
  }
}

package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.dto.ImageData;
import dev.thiagooliveira.tablesplit.application.menu.dto.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import java.util.ArrayList;
import java.util.UUID;

public class UpdateItem {

  private final GetItem getItem;
  private final ImageStorage imageStorage;
  private final ItemRepository itemRepository;

  public UpdateItem(GetItem getItem, ImageStorage imageStorage, ItemRepository itemRepository) {
    this.getItem = getItem;
    this.imageStorage = imageStorage;
    this.itemRepository = itemRepository;
  }

  public void execute(UUID restaurantId, UUID itemId, UpdateItemCommand command) {
    var item = this.getItem.execute(restaurantId, itemId).orElseThrow();
    item.setPrice(command.price());
    item.setDescription(command.description());
    item.setName(command.name());
    item.setCategoryId(command.categoryId());

    var imageIdsToKeep =
        command.imageIdsToKeep() == null ? new ArrayList<>() : command.imageIdsToKeep();

    item.getImages()
        .removeIf(
            img -> {
              if (!imageIdsToKeep.contains(img.getId())) {
                imageStorage.deleteItem(restaurantId, itemId, img.getId());
                return true;
              }
              return false;
            });

    if (command.images() != null && command.images().newImages() != null) {
      for (ImageData i : command.images().newImages()) {
        var image = new ItemImage();
        image.setId(UUID.randomUUID());

        boolean hasMain =
            item.getImages().stream()
                .anyMatch(img -> imageIdsToKeep.contains(img.getId()) && img.isMain());
        image.setMain(!hasMain && command.images().newImages().indexOf(i) == 0);
        image.setItemId(item.getId());
        image.setName(
            imageStorage.uploadItem(i, item.getRestaurantId(), item.getId(), image.getId()));
        item.getImages().add(image);
      }
    }
    this.itemRepository.save(item);
  }
}

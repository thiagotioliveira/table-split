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
  private final ItemImageRepository itemImageRepository;
  private final ItemRepository itemRepository;

  public UpdateItem(
      GetItem getItem,
      ImageStorage imageStorage,
      ItemImageRepository itemImageRepository,
      ItemRepository itemRepository) {
    this.getItem = getItem;
    this.imageStorage = imageStorage;
    this.itemImageRepository = itemImageRepository;
    this.itemRepository = itemRepository;
  }

  public void execute(UUID restaurantId, UUID itemId, UpdateItemCommand command) {
    var item = this.getItem.execute(restaurantId, itemId).orElseThrow();
    item.setPrice(command.price());
    item.setDescription(command.description());
    item.setName(command.name());
    item.setCategoryId(command.categoryId());
    this.itemRepository.save(item);
    var imageIdsToKeep =
        command.imageIdsToKeep() == null ? new ArrayList<>() : command.imageIdsToKeep();
    item.getImages().stream()
        .filter(img -> !imageIdsToKeep.contains(img.getId()))
        .forEach(
            img -> {
              var result = imageStorage.deleteItem(restaurantId, itemId, img.getId());
              itemImageRepository.delete(img.getId());
            });

    if (command.images() != null && command.images().newImages() != null) {
      for (ImageData i : command.images().newImages()) {
        var image = new ItemImage();
        image.setId(UUID.randomUUID());
        // For now, keep the simple logic, but ensure it doesn't break if already has
        // main
        boolean hasMain =
            item.getImages().stream()
                .anyMatch(img -> command.imageIdsToKeep().contains(img.getId()) && img.isMain());
        image.setMain(!hasMain && command.images().newImages().indexOf(i) == 0);
        image.setItemId(item.getId());
        image.setName(
            imageStorage.uploadItem(i, item.getRestaurantId(), item.getId(), image.getId()));
        this.itemImageRepository.save(image);
      }
    }
  }
}
